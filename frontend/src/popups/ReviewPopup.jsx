import React, { useEffect, useState } from 'react';
import './ReviewPopup.css';

const ReviewPopup = ({ movieTitle, initialRating = null, initialComment = '', onClose, onSubmit }) => {
  const [rating, setRating] = useState(initialRating ?? 0);
  const [hover, setHover] = useState(0);
  const [comment, setComment] = useState(initialComment ?? "");

  useEffect(() => {
    setRating(initialRating ?? 0);
    setComment(initialComment ?? '');
    setHover(0);
  }, [initialRating, initialComment, movieTitle]);

  const handleSubmit = () => {
    // On renvoie les données au parent qui fera l'appel API
    onSubmit({ score: rating, content: comment });
  };

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <button
          type="button"
          className="close-btn"
          onClick={onClose}
          aria-label="Fermer"
        >
          ×
        </button>

        <h2 className="popup-title">Votre critique de : <span>{movieTitle}</span></h2>

        {/* Système d'étoiles */}
        <div className="star-rating">
          {[...Array(5)].map((star, index) => {
            const ratingValue = index + 1;
            return (
              <button
                type="button"
                key={index}
                className={ratingValue <= (hover || rating) ? "on" : "off"}
                onClick={() => setRating(ratingValue)}
                onMouseEnter={() => setHover(ratingValue)}
                onMouseLeave={() => setHover(0)}
              >
                <span className="star">&#9733;</span>
              </button>
            );
          })}
        </div>

        <textarea
          placeholder="Écrivez votre critique ici..."
          value={comment}
          onChange={(e) => setComment(e.target.value)}
        />

        <button
          className="submit-btn"
          disabled={rating === 0 || comment.trim().length === 0}
          onClick={handleSubmit}
        >
          Envoyer ma note
        </button>
      </div>
    </div>
  );
};

export default ReviewPopup;