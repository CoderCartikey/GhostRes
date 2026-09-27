from __future__ import annotations

from datetime import datetime
from pathlib import Path
import uuid

import streamlit as st

from ghostres_edge.engines.cuda_realesrgan import CudaRealESRGANEngine
from ghostres_edge.video import enhance_video


PROJECT_DIR = Path(__file__).resolve().parent
REALESRGAN_ROOT = Path(r"C:\Users\ASUS\Real-ESRGAN")
WEIGHTS_DIR = PROJECT_DIR / "weights"
UPLOAD_DIR = PROJECT_DIR / "uploads"
OUTPUT_DIR = PROJECT_DIR / "outputs"

for directory in (UPLOAD_DIR, OUTPUT_DIR):
    directory.mkdir(exist_ok=True)


@st.cache_resource(show_spinner=False)
def load_engine(scale: int) -> CudaRealESRGANEngine:
    weights_path = WEIGHTS_DIR / f"RealESRGAN_x{scale}plus.pth"

    return CudaRealESRGANEngine(
        realesrgan_root=REALESRGAN_ROOT,
        weights_path=weights_path,
        scale=scale,
        tile=128,
        tile_pad=10,
        half=False,
    )


def save_upload(uploaded_file) -> Path:
    extension = Path(uploaded_file.name).suffix.lower() or ".mp4"
    unique_name = (
        f"{datetime.now():%Y%m%d_%H%M%S}_"
        f"{uuid.uuid4().hex[:8]}{extension}"
    )
    destination = UPLOAD_DIR / unique_name
    destination.write_bytes(uploaded_file.getvalue())
    return destination


def render_results(result, output_path: Path, scale: int) -> None:
    st.success("Restoration complete.")

    left, middle, right, fourth = st.columns(4)
    left.metric("Output resolution", result.output_resolution)
    middle.metric("AI latency", f"{result.ms_per_frame:.0f} ms/frame")
    right.metric("AI throughput", f"{result.ai_fps:.2f} FPS")
    fourth.metric("Total time", f"{result.processing_seconds:.2f} s")

    st.caption(
        f"{result.input_resolution} → {result.output_resolution} · "
        f"{result.processed_frames}/{result.total_frames} frames · "
        f"CUDA Real-ESRGAN x{scale}plus"
    )

    output_bytes = output_path.read_bytes()
    st.video(output_bytes)

    st.download_button(
        label="Download restored video",
        data=output_bytes,
        file_name=output_path.name,
        mime="video/mp4",
        use_container_width=True,
    )


def main() -> None:
    st.set_page_config(
        page_title="GhostRes Edge",
        page_icon="✦",
        layout="wide",
    )

    st.markdown(
        """
        <style>
        .block-container { max-width: 1050px; padding-top: 3rem; }
        .stButton > button, .stDownloadButton > button {
            width: 100%;
            border-radius: 8px;
            font-weight: 600;
        }
        </style>
        """,
        unsafe_allow_html=True,
    )

    st.title("GhostRes Edge")
    st.caption(
        "Local AI video restoration for low-resolution footage — "
        "running on your device."
    )

    st.info(
        "Prototype note: restoration generates plausible visual detail; "
        "it is not forensic verification. Current output does not preserve audio."
    )

    uploaded_file = st.file_uploader(
        "Choose a video",
        type=["mp4", "avi", "mov", "mkv"],
        help="Start with smoke_input.mp4 for the one-frame test.",
    )

    quality_label = st.radio(
        "Restoration mode",
        options=["2× Fast", "4× Quality"],
        horizontal=True,
        help="2× is much faster. 4× produces the maximum available output resolution.",
    )
    scale = 2 if quality_label == "2× Fast" else 4

    if uploaded_file is not None:
        st.video(uploaded_file)

    start = st.button(
        f"Restore video at {scale}×",
        disabled=uploaded_file is None,
        type="primary",
    )

    if start:
        if not REALESRGAN_ROOT.is_dir():
            st.error(f"Real-ESRGAN folder not found: {REALESRGAN_ROOT}")
            return

        input_path = save_upload(uploaded_file)
        output_name = (
            f"ghostres_{scale}x_{datetime.now():%Y%m%d_%H%M%S}.mp4"
        )
        output_path = OUTPUT_DIR / output_name

        try:
            with st.spinner("Loading model and restoring video frames…"):
                engine = load_engine(scale)
                result = enhance_video(
                    input_path=input_path,
                    output_path=output_path,
                    engine=engine,
                )
        except Exception as error:
            st.exception(error)
            return

        st.session_state["last_result"] = (result, output_path, scale)

    if "last_result" in st.session_state:
        st.divider()
        result, output_path, scale = st.session_state["last_result"]
        if output_path.is_file():
            render_results(result, output_path, scale)


if __name__ == "__main__":
    main()