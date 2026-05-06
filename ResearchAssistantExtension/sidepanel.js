const API_BASE_URL = 'https://research-assistant-lub2.onrender.com'; // Change back to http://localhost:8080 for local testing

let currentNoteId = null; // Store the ID of the note we are editing
document.addEventListener('DOMContentLoaded', () => {
    // Fetch the saved note from the database instead of local storage
    fetchNotesFromDB();

    // Event listeners
    document.getElementById('summarizeBtn').addEventListener('click', () => processContent('summarize'));
    document.getElementById('suggestBtn').addEventListener('click', () => processContent('suggest'));
    document.getElementById('saveNotesBtn').addEventListener('click', saveNotesToDB);
    
    // History UI Event Listeners
    document.getElementById('toggleHistoryBtn').addEventListener('click', toggleHistory);
    document.getElementById('newNoteBtn').addEventListener('click', createNewNote);
});

async function fetchNotesFromDB() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/notes`, {headers:
                {
                    'x-api-key': API_KEY
                }
        });
        if (response.ok) {
            const notes = await response.json();
            // If we have saved notes, load the most recent one into the text area
            if (notes.length > 0) {
                const latestNote = notes[notes.length - 1]; // Get the last item
                currentNoteId = latestNote.id; // Remember the ID
                document.getElementById('notes').value = latestNote.content;
            }
        }
    } catch (error) {
        console.error("Could not load notes from database", error);
    }
}

async function processContent(operation) {
    const loaderContainer = document.getElementById('loader-container');
    const resultsContainer = document.getElementById('results');
    
    try {
        // Hide previous results, show loader
        resultsContainer.classList.add('hidden');
        resultsContainer.innerHTML = '';
        loaderContainer.classList.remove('hidden');

        // Get selected text
        const [tab] = await chrome.tabs.query({
            active: true, 
            currentWindow: true
        });
        
        const [{result}] = await chrome.scripting.executeScript({
            target: {tabId: tab.id},
            function: () => window.getSelection().toString()
        });

        if (!result || result.trim() === '') {
            showError('Please select some text on the page first.');
            return;
        }

        // Call backend
        const response = await fetch(`${API_BASE_URL}/api/research/process`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'x-api-key': API_KEY
            },
            body: JSON.stringify({content: result, operation: operation})
        });

        if (!response.ok) {
            throw new Error(`API Error: ${response.status} ${response.statusText}`);
        }

        const text = await response.text();
        showResult(text.replace(/\n/g, '<br>'));

    } catch (error) {
        // Handle specific fetch errors (backend down)
        if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
            showError('Backend server is unreachable. Please ensure the Spring Boot server is running on localhost:8080.');
        } else {
            showError(`An error occurred: ${error.message}`);
        }
    } finally {
        // Always hide loader when done
        loaderContainer.classList.add('hidden');
    }
}

async function saveNotesToDB() {
    const notesContent = document.getElementById('notes').value;
    const saveBtn = document.getElementById('saveNotesBtn');
    
    try {
        let url = `${API_BASE_URL}/api/notes`;
        let method = 'POST'; // Default to creating a new note
        
        // If we already have a note loaded, we UPDATE it instead of creating a new one
        if (currentNoteId !== null) {
            url = `${API_BASE_URL}/api/notes/${currentNoteId}`;
            method = 'PUT';
        }

        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'x-api-key': API_KEY
            },
            body: JSON.stringify({ content: notesContent, title: "My Workspace" })
        });

        if (response.ok) {
            const savedNote = await response.json();
            currentNoteId = savedNote.id; // Update our ID just in case it was a new POST

            // Visual feedback
            const originalText = saveBtn.innerText;
            saveBtn.innerText = 'Saved to DB! ✓';
            saveBtn.style.backgroundColor = '#059669'; 
            
            setTimeout(() => {
                saveBtn.innerText = originalText;
                saveBtn.style.backgroundColor = ''; 
            }, 2000);
        } else {
            throw new Error("Failed to save to database");
        }
    } catch (error) {
        alert("Error saving notes: Make sure your Spring Boot server is running!");
    }
}

function showResult(content) {
    const resultsContainer = document.getElementById('results');
    resultsContainer.innerHTML = `
        <div class="result-item">
            <div class="result-content">${content}</div>
            <div style="margin-top: 15px; text-align: right;">
                <button class="btn secondary-btn add-to-notes-btn" style="font-size: 0.75rem; padding: 6px 12px; display: inline-flex; align-items: center; gap: 4px;">
                    📝 Add to Notes
                </button>
            </div>
        </div>
    `;
    
    // Add event listener to the new button
    resultsContainer.querySelector('.add-to-notes-btn').addEventListener('click', (e) => {
        const notesArea = document.getElementById('notes');
        const currentNotes = notesArea.value;
        const separator = currentNotes.trim() ? '\n\n---\n\n' : '';
        
        // Convert <br> tags back to newlines for the textarea
        const plainText = content.replace(/<br>/g, '\n');
        
        // Append the new text
        notesArea.value = currentNotes + separator + plainText;
        
        // Scroll to the bottom of the textarea
        notesArea.scrollTop = notesArea.scrollHeight;
        
        // Visual feedback on the button
        const btn = e.target;
        const originalHtml = btn.innerHTML;
        btn.innerHTML = '✓ Added!';
        btn.style.backgroundColor = '#10b981';
        btn.style.color = 'white';
        btn.style.borderColor = '#10b981';
        
        setTimeout(() => {
            btn.innerHTML = originalHtml;
            btn.style.backgroundColor = '';
            btn.style.color = '';
            btn.style.borderColor = '';
        }, 2000);
    });

    resultsContainer.classList.remove('hidden');
}

function showError(message) {
    const resultsContainer = document.getElementById('results');
    resultsContainer.innerHTML = `<div class="error-msg">⚠️ ${message}</div>`;
    resultsContainer.classList.remove('hidden');
}

// --- History UI Functions ---

async function toggleHistory() {
    const historyContainer = document.getElementById('history-container');
    const workspace = document.getElementById('workspace');
    const toggleBtn = document.getElementById('toggleHistoryBtn');
    
    if (historyContainer.classList.contains('hidden')) {
        // Switch to History View
        historyContainer.classList.remove('hidden');
        workspace.classList.add('hidden');
        toggleBtn.innerText = '✏️ Back to Editor';
        await loadHistory(); // Fetch from DB!
    } else {
        // Switch to Editor View
        historyContainer.classList.add('hidden');
        workspace.classList.remove('hidden');
        toggleBtn.innerText = '📜 History';
    }
}

async function loadHistory() {
    const historyList = document.getElementById('history-list');
    historyList.innerHTML = '<p class="loader-text" style="text-align: center;">Loading...</p>';
    
    try {
        const response = await fetch(`${API_BASE_URL}/api/notes`, {
            headers: {
                'x-api-key': API_KEY
            }
        });
        if (response.ok) {
            const notes = await response.json();
            historyList.innerHTML = ''; 
            
            if (notes.length === 0) {
                historyList.innerHTML = '<p style="font-size: 0.8rem; color: #64748b; text-align: center;">No notes found.</p>';
                return;
            }

            // Create list items for each note (reverse to show newest first)
            notes.reverse().forEach(note => {
                const li = document.createElement('li');
                li.className = 'history-item';
                
                // Show first 30 characters as a preview title
                const preview = note.content ? note.content.substring(0, 30) + '...' : 'Empty Note';
                
                // Clickable Title
                const titleSpan = document.createElement('span');
                titleSpan.className = 'history-title';
                titleSpan.innerText = preview;
                titleSpan.onclick = () => openNote(note.id, note.content); // Load into editor

                // Delete Button
                const deleteBtn = document.createElement('button');
                deleteBtn.className = 'delete-btn';
                deleteBtn.innerText = '🗑️';
                deleteBtn.onclick = (e) => deleteNote(note.id, e); // Delete from DB

                li.appendChild(titleSpan);
                li.appendChild(deleteBtn);
                historyList.appendChild(li); // Add to UI
            });
        }
    } catch (error) {
        historyList.innerHTML = '<p class="error-msg">Failed to load history.</p>';
    }
}

function openNote(id, content) {
    currentNoteId = id;
    document.getElementById('notes').value = content;
    toggleHistory(); // Switch view back to editor
}

function createNewNote() {
    currentNoteId = null; // Clear the ID so the next Save acts as a POST (Create)
    document.getElementById('notes').value = '';
}

async function deleteNote(id, event) {
    if (!confirm("Are you sure you want to delete this note?")) return;

    try {
        // Send DELETE request to your Spring Boot API
        const response = await fetch(`${API_BASE_URL}/api/notes/${id}`, {
            method: 'DELETE',
            headers: {
                'x-api-key': API_KEY
            }
        });
        
        if (response.ok) {
            if (currentNoteId === id) createNewNote(); // If they deleted the open note, clear editor
            await loadHistory();  // Refresh the UI list
        } else {
            alert("Failed to delete note.");
        }
    } catch (error) {
        alert("Error deleting note.");
    }
}