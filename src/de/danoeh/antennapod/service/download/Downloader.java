package de.danoeh.antennapod.service.download;

import android.os.Handler;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.asynctask.DownloadStatus;

/** Downloads files */
public abstract class Downloader extends Thread {
	private static final String TAG = "Downloader";
	private Handler handler;
	private DownloadService downloadService;

	protected boolean finished;
	
	protected volatile boolean cancelled;

	protected volatile DownloadStatus status;

	public Downloader(DownloadService downloadService, DownloadStatus status) {
		super();
		this.downloadService = downloadService;
		this.status = status;
		this.status.setStatusMsg(R.string.download_pending);
		this.cancelled = false;
		handler = new Handler();
	}

	/**
	 * This method must be called when the download was completed, failed, or
	 * was cancelled
	 */
	protected void finish() {
		if (!finished) {
			finished = true;
			handler.post(new Runnable() {

				@Override
				public void run() {
					downloadService.onDownloadCompleted(Downloader.this);
				}

			});
		}
	}

	protected abstract void download();

	@Override
	public final void run() {
		download();
		finish();
	}

	public DownloadStatus getStatus() {
		return status;
	}
	
	public void cancel() {
		cancelled = true;
	}

}