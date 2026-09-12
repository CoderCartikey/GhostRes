from __future__ import annotations

from abc import ABC, abstractmethod
import numpy as np


class EnhancementEngine(ABC):
    """Common contract for every GhostRes Edge inference backend."""

    name: str
    scale: int

    @abstractmethod
    def enhance(self, frame: np.ndarray) -> np.ndarray:
        """Return one enhanced BGR video frame."""
        raise NotImplementedError