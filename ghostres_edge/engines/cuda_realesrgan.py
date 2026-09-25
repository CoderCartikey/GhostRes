from __future__ import annotations

from pathlib import Path
import sys

import numpy as np

from .base import EnhancementEngine


class CudaRealESRGANEngine(EnhancementEngine):
    """
    GhostRes v1 inference, wrapped as a reusable Edge backend.

    This intentionally preserves the proven x4 configuration:
    tile=128, tile_pad=10, half=False.
    """

    name = "CUDA Real-ESRGAN x4plus"
    scale = 4

    def __init__(
        self,
        *,
        realesrgan_root: Path,
        weights_path: Path,
        scale: int = 4,
        tile: int = 128,
        tile_pad: int = 10,
        half: bool = False,
    ) -> None:
        if scale not in (2, 4):
            raise ValueError(f"Unsupported scale: {scale}. Must be 2 or 4.")
        self.scale = scale
        self.name = f"CUDA Real-ESRGAN x{scale}plus"

        root = realesrgan_root.resolve()
        weights = weights_path.resolve()

        if not root.exists():
            raise FileNotFoundError(f"Real-ESRGAN folder not found: {root}")
        if not weights.is_file():
            raise FileNotFoundError(f"Model weights not found: {weights}")

        root_text = str(root)
        if root_text not in sys.path:
            sys.path.insert(0, root_text)

        if sys.platform == "win32":
            import os
            for candidate in [
                Path(sys.prefix) / "Library" / "bin",
                Path(sys.prefix).parent.parent / "Library" / "bin",
            ]:
                if candidate.is_dir():
                    try:
                        os.add_dll_directory(str(candidate))
                    except (OSError, AttributeError):
                        pass

        from basicsr.archs.rrdbnet_arch import RRDBNet
        from realesrgan import RealESRGANer

        model = RRDBNet(
            num_in_ch=3,
            num_out_ch=3,
            num_feat=64,
            num_block=23,
            num_grow_ch=32,
            scale=self.scale,
        )

        try:
            self._upsampler = RealESRGANer(
                scale=self.scale,
                model_path=str(weights),
                model=model,
                tile=tile,
                tile_pad=tile_pad,
                pre_pad=0,
                half=half,
            )
        except Exception as e:
            raise RuntimeError(
                f"Failed to load Real-ESRGAN x{scale} model weights from '{weights}': {e}"
            ) from e

    def enhance(self, frame: np.ndarray) -> np.ndarray:
        enhanced, _ = self._upsampler.enhance(frame, outscale=self.scale)
        return enhanced