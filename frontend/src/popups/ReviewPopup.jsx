import { useEffect, useCallback, useState } from 'react';
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

  const handleKeyDown = useCallback((e) => {
    if (e.key === 'Escape') onClose();
  }, [onClose]);

  useEffect(() => {
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [handleKeyDown]);

  const handleSubmit = () => {
    onSubmit({ score: rating, content: comment });
  };

  return (
    <div
      className="popup-overlay"
      onClick={onClose}
      role="presentation"
    >
      <div
        className="popup-content"
        role="dialog"
        aria-modal="true"
        aria-label={`Critique de ${movieTitle}`}
        onClick={(e) => e.stopPropagation()}
      >
        <button
          type="button"
          className="close-btn"
          onClick={onClose}
          aria-label="Fermer"
        >
          ×
        </button>

        <h2 className="popup-title">Votre critique de : <span>{movieTitle}</span></h2>

        <div className="star-rating" role="radiogroup" aria-label="Note sur 5">
          {[...Array(5)].map((_, index) => {
            const ratingValue = index + 1;
            return (
              <button
                type="button"
                key={index}
                className={ratingValue <= (hover || rating) ? "on" : "off"}
                onClick={() => setRating(ratingValue)}
                onMouseEnter={() => setHover(ratingValue)}
                onMouseLeave={() => setHover(0)}
                aria-label={`${ratingValue} étoile${ratingValue > 1 ? 's' : ''}`}
              >
                <span className="star">&#9733;</span>
              </button>
            );
          })}
        </div>

        <label htmlFor="review-comment" className="sr-only">Votre critique</label>
        <textarea
          id="review-comment"
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