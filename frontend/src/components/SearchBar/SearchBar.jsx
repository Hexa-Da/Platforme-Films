import { useState } from 'react';
import './SearchBar.css'


// "onSearch" est une fonction que le composant parent (Movies) va lui passer
function SearchBar({
  onSearch,
  placeholder = 'Rechercher un film...',
  ariaLabel,
}) {
    const [searchTerm, setSearchTerm] = useState('');

    const handleChange = (event) => {
        const text = event.target.value; // Récupère le texte tapé
        setSearchTerm(text);             // Met à jour l'affichage de la barre

        if (onSearch) {
          onSearch(text);
        }
    };

    return (
        <div className="search-bar-container">
          <input
            type="text"
            className="search-input"
            placeholder={placeholder}
            aria-label={ariaLabel ?? placeholder}
            value={searchTerm}
            onChange={handleChange}
          />
        </div>
    );
}

export default SearchBar;