from __future__ import annotations

import os
import threading
import tkinter as tk
from datetime import datetime
from pathlib import Path
from tkinter import filedialog, messagebox

import cv2
import customtkinter as ctk

from ghostres_edge.engines.cuda_realesrgan import CudaRealESRGANEngine
from ghostres_edge.video import VideoResult, enhance_video


PROJECT_DIR = Path(__file__).resolve().parent
REALESRGAN_ROOT = Path(r"C:\Users\ASUS\Real-ESRGAN")
WEIGHTS_DIR = PROJECT_DIR / "weights"
OUTPUT_DIR = PROJECT_DIR / "outputs"
OUTPUT_DIR.mkdir(exist_ok=True)


ctk.set_appearance_mode("dark")
ctk.set_default_color_theme("blue")


class GhostResDesktop(ctk.CTk):
    def __init__(self) -> None:
        super().__init__()

        self.title("GhostRes Edge")
        self.geometry("940x690")
        self.minsize(860, 620)

        self.selected_video: Path | None = None
        self.last_output: Path | None = None
        self.mode_var = tk.StringVar(value="2× Fast")

        self.grid_columnconfigure(0, weight=1)
        self._build_ui()

    def _build_ui(self) -> None:
        header = ctk.CTkFrame(self, fg_color="transparent")
        header.grid(row=0, column=0, sticky="ew", padx=32, pady=(28, 12))
        header.grid_columnconfigure(0, weight=1)

        ctk.CTkLabel(
            header,
            text="GhostRes Edge",
            font=ctk.CTkFont(size=30, weight="bold"),
        ).grid(row=0, column=0, sticky="w")

        ctk.CTkLabel(
            header,
            text="Local AI video restoration for low-resolution footage",
            text_color="#A6ADB8",
            font=ctk.CTkFont(size=14),
        ).grid(row=1, column=0, sticky="w", pady=(3, 0))

        ctk.CTkLabel(
            header,
            text="Desktop demo: NVIDIA CUDA  •  Target evidence: Snapdragon X Elite NPU profile",
            text_color="#7CBEFF",
            font=ctk.CTkFont(size=12),
        ).grid(row=2, column=0, sticky="w", pady=(8, 0))

        file_card = ctk.CTkFrame(self, corner_radius=12)
        file_card.grid(row=1, column=0, sticky="ew", padx=32, pady=12)
        file_card.grid_columnconfigure(0, weight=1)

        ctk.CTkLabel(
            file_card,
            text="1. Select video",
            font=ctk.CTkFont(size=17, weight="bold"),
        ).grid(row=0, column=0, sticky="w", padx=20, pady=(18, 3))

        self.file_name_label = ctk.CTkLabel(
            file_card,
            text="No video selected",
            text_color="#A6ADB8",
            anchor="w",
        )
        self.file_name_label.grid(row=1, column=0, sticky="ew", padx=20)

        self.video_info_label = ctk.CTkLabel(
            file_card,
            text="Start with smoke_input.mp4 for the one-frame test.",
            text_color="#777F8C",
            anchor="w",
        )
        self.video_info_label.grid(row=2, column=0, sticky="ew", padx=20, pady=(2, 18))

        ctk.CTkButton(
            file_card,
            text="Choose video",
            command=self.choose_video,
            width=140,
        ).grid(row=0, column=1, rowspan=3, padx=20, pady=20)

        mode_card = ctk.CTkFrame(self, corner_radius=12)
        mode_card.grid(row=2, column=0, sticky="ew", padx=32, pady=12)
        mode_card.grid_columnconfigure(0, weight=1)

        ctk.CTkLabel(
            mode_card,
            text="2. Choose restoration mode",
            font=ctk.CTkFont(size=17, weight="bold"),
        ).grid(row=0, column=0, sticky="w", padx=20, pady=(18, 8))

        ctk.CTkSegmentedButton(
            mode_card,
            values=["2× Fast", "4× Quality"],
            variable=self.mode_var,
        ).grid(row=1, column=0, sticky="ew", padx=20)

        ctk.CTkLabel(
            mode_card,
            text="2× is faster for demos. 4× maximizes output resolution.",
            text_color="#A6ADB8",
            anchor="w",
        ).grid(row=2, column=0, sticky="w", padx=20, pady=(8, 18))

        action_card = ctk.CTkFrame(self, corner_radius=12)
        action_card.grid(row=3, column=0, sticky="ew", padx=32, pady=12)
        action_card.grid_columnconfigure(0, weight=1)

        self.restore_button = ctk.CTkButton(
            action_card,
            text="Restore video",
            command=self.start_restoration,
            height=44,
            font=ctk.CTkFont(size=16, weight="bold"),
        )
        self.restore_button.grid(row=0, column=0, sticky="ew", padx=20, pady=(20, 10))

        self.progress = ctk.CTkProgressBar(action_card, mode="indeterminate")
        self.progress.grid(row=1, column=0, sticky="ew", padx=20)
        self.progress.set(0)

        self.status_label = ctk.CTkLabel(
            action_card,
            text="Ready. The current prototype restores video frames and does not preserve audio.",
            text_color="#A6ADB8",
            anchor="w",
        )
        self.status_label.grid(row=2, column=0, sticky="w", padx=20, pady=(10, 20))

        result_card = ctk.CTkFrame(self, corner_radius=12)
        result_card.grid(row=4, column=0, sticky="nsew", padx=32, pady=(12, 28))
        result_card.grid_columnconfigure(0, weight=1)

        ctk.CTkLabel(
            result_card,
            text="Result",
            font=ctk.CTkFont(size=17, weight="bold"),
        ).grid(row=0, column=0, sticky="w", padx=20, pady=(18, 8))

        self.metrics_label = ctk.CTkLabel(
            result_card,
            text="No completed restoration yet.",
            justify="left",
            anchor="w",
            text_color="#A6ADB8",
            font=ctk.CTkFont(size=14),
        )
        self.metrics_label.grid(row=1, column=0, sticky="ew", padx=20)

        self.output_label = ctk.CTkLabel(
            result_card,
            text="",
            text_color="#7CBEFF",
            anchor="w",
            wraplength=780,
        )
        self.output_label.grid(row=2, column=0, sticky="ew", padx=20, pady=(8, 14))

        buttons = ctk.CTkFrame(result_card, fg_color="transparent")
        buttons.grid(row=3, column=0, sticky="ew", padx=20, pady=(0, 20))
        buttons.grid_columnconfigure((0, 1), weight=1)

        self.open_video_button = ctk.CTkButton(
            buttons,
            text="Open restored video",
            state="disabled",
            command=self.open_restored_video,
        )
        self.open_video_button.grid(row=0, column=0, sticky="ew", padx=(0, 6))

        self.open_folder_button = ctk.CTkButton(
            buttons,
            text="Open output folder",
            state="disabled",
            command=lambda: os.startfile(str(OUTPUT_DIR)),
        )
        self.open_folder_button.grid(row=0, column=1, sticky="ew", padx=(6, 0))

    def choose_video(self) -> None:
        file_name = filedialog.askopenfilename(
            title="Choose a video for restoration",
            filetypes=[
                ("Video files", "*.mp4 *.avi *.mov *.mkv"),
                ("All files", "*.*"),
            ],
        )
        if not file_name:
            return

        self.selected_video = Path(file_name)
        self.file_name_label.configure(text=self.selected_video.name)

        cap = cv2.VideoCapture(str(self.selected_video))
        try:
            width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
            height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
            fps = cap.get(cv2.CAP_PROP_FPS)
            frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
        finally:
            cap.release()

        self.video_info_label.configure(
            text=f"{width}×{height}  •  {fps:.2f} FPS  •  {frames} frames"
        )
        self.status_label.configure(
            text="Video selected. Choose a mode and start restoration.",
            text_color="#A6ADB8",
        )

    def start_restoration(self) -> None:
        if self.selected_video is None:
            messagebox.showwarning("GhostRes Edge", "Select a video first.")
            return

        scale = 2 if self.mode_var.get() == "2× Fast" else 4
        weights_path = WEIGHTS_DIR / f"RealESRGAN_x{scale}plus.pth"

        if not REALESRGAN_ROOT.is_dir():
            messagebox.showerror(
                "GhostRes Edge",
                f"Real-ESRGAN folder was not found:\n{REALESRGAN_ROOT}",
            )
            return

        if not weights_path.is_file():
            messagebox.showerror(
                "GhostRes Edge",
                f"Model weights were not found:\n{weights_path}",
            )
            return

        output_path = OUTPUT_DIR / (
            f"ghostres_{scale}x_{datetime.now():%Y%m%d_%H%M%S}.mp4"
        )

        self.restore_button.configure(state="disabled")
        self.progress.grid()
        self.progress.set(0)
        self.progress.start()
        self.status_label.configure(
            text=f"Loading CUDA Real-ESRGAN x{scale} model and restoring frames…",
            text_color="#7CBEFF",
        )

        thread = threading.Thread(
            target=self.restore_worker,
            args=(self.selected_video, output_path, scale, weights_path),
            daemon=True,
        )
        thread.start()

    def restore_worker(
        self,
        input_path: Path,
        output_path: Path,
        scale: int,
        weights_path: Path,
    ) -> None:
        try:
            engine = CudaRealESRGANEngine(
                realesrgan_root=REALESRGAN_ROOT,
                weights_path=weights_path,
                scale=scale,
                tile=128,
                tile_pad=10,
                half=False,
            )
            result = enhance_video(
                input_path=input_path,
                output_path=output_path,
                engine=engine,
            )
            self.after(
                0,
                lambda r=result, p=output_path, s=scale: self.restore_complete(r, p, s),
            )
        except Exception as error:
            message = f"{type(error).__name__}: {error}"
            self.after(0, self.restore_failed, message)

    def restore_complete(
        self,
        result: VideoResult,
        output_path: Path,
        scale: int,
    ) -> None:
        self.progress.stop()
        self.progress.grid_remove()
        self.restore_button.configure(state="normal")

        self.last_output = output_path
        self.status_label.configure(
            text="Restoration complete. Output is ready.",
            text_color="#47D67A",
        )
        self.metrics_label.configure(
            text=(
                f"{result.input_resolution}  →  {result.output_resolution}\n"
                f"{result.processed_frames}/{result.total_frames} frames  •  "
                f"CUDA Real-ESRGAN x{scale}plus\n"
                f"AI latency: {result.ms_per_frame:.0f} ms/frame  •  "
                f"AI throughput: {result.ai_fps:.2f} FPS  •  "
                f"Total time: {result.processing_seconds:.2f} s"
            ),
            text_color="#E7EAF0",
        )
        self.output_label.configure(
            text=f"Output ready: outputs\\{output_path.name}  •  Last run: {scale}×"
                                    )
        self.open_video_button.configure(state="normal")
        self.open_folder_button.configure(state="normal")

    def restore_failed(self, message: str) -> None:
        self.progress.stop()
        self.progress.grid_remove()
        self.restore_button.configure(state="normal")
        self.status_label.configure(
            text="Restoration failed. Read the error message and terminal output.",
            text_color="#FF7777",
        )
        messagebox.showerror("GhostRes Edge", message)

    def open_restored_video(self) -> None:
        if self.last_output is not None and self.last_output.is_file():
            os.startfile(str(self.last_output))


if __name__ == "__main__":
    app = GhostResDesktop()
    app.mainloop()