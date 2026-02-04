button = document.getElementById('load-all');
button.addEventListener('click', () => {
    fetch('http://localhost:8080/characters')
    .then(response => response.json())
    .then(data => { 
        data.forEach(character => {
            const li = document.createElement('li');
            li.textContent = character.id + ' - ' + character.firstname + ' ' + character.lastname + ' - ' + character.universe;
            document.getElementById('load-all-results').appendChild(li);
        }); 
    }); 
});

button = document.getElementById('search-name');
button.addEventListener('click', () => {
    fetch('http://localhost:8080/character/' + document.getElementById('name-input').value)
    .then(response => response.json())
    .then(data => { 
        const li = document.createElement('li');
        li.textContent = data.id + ' - ' + data.firstname + ' ' + data.lastname + ' - ' + data.universe;
        document.getElementById('search-name-results').appendChild(li);
    });
});

button = document.getElementById('search-id');
button.addEventListener('click', () => {
    fetch('http://localhost:8080/characterById/' + document.getElementById('id-input').value)
    .then(response => response.json())
    .then(data => {
        const li = document.createElement('li');
        li.textContent = data.id + ' - ' + data.firstname + ' ' + data.lastname + ' - ' + data.universe;
        document.getElementById('search-id-results').appendChild(li);
    });
});

button = document.getElementById('add-character');
button.addEventListener('click', () => {
    fetch('http://localhost:8080/addCharacter', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: null,
            firstname: document.getElementById('firstname-add-input').value,
            lastname: document.getElementById('lastname-add-input').value,
            universe: document.getElementById('universe-add-input').value    
        })
    })
    .then(response => response.json())
    .then(data => { 
        const li = document.createElement('li');
        li.textContent = data.id + ' - ' + data.firstname + ' ' + data.lastname + ' - ' + data.universe + ' - Personnage ajouté';
        document.getElementById('add-results').appendChild(li);
    });
});

button = document.getElementById('update-character');
button.addEventListener('click', () => {
    fetch('http://localhost:8080/update', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id: document.getElementById('id-update-input').value,
            firstname: document.getElementById('firstname-update-input').value,
            lastname: document.getElementById('lastname-update-input').value,
            universe: document.getElementById('universe-update-input').value
        })
    })
    .then(response => response.json())
    .then(data => {
        const li = document.createElement('li');
        li.textContent = data.id + ' - ' + data.firstname + ' ' + data.lastname + ' - ' + data.universe;
        document.getElementById('update-results').appendChild(li);
    });
});

button = document.getElementById('delete-character');
button.addEventListener('click', () => {
    fetch('http://localhost:8080/delete/' + document.getElementById('id-delete-input').value, {
        method: 'DELETE'
    })
    .then(response => response.json())
    .then(data => {
        const li = document.createElement('li');
        li.textContent = data.firstname + ' ' + data.lastname + ' - Personnage supprimé';
        document.getElementById('delete-results').appendChild(li);    
    });
});