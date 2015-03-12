package eu.over9000.skadi.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.ImageView;

public class ImageRetrievalService extends Service<ImageView> {

	private final String url;
	private int width;
	private int height;
	private boolean resize = false;
	
	public ImageRetrievalService(final String url) {
		this.url = url;
	}
	
	public ImageRetrievalService(final String url, final int width, final int height) {
		this.url = url;
		this.width = width;
		this.height = height;
		this.resize = true;
	}
	
	@Override
	protected Task<ImageView> createTask() {
		return new Task<ImageView>() {

			@Override
			protected ImageView call() throws Exception {
				final ImageView iv = new ImageView(ImageRetrievalService.this.url);

				if (ImageRetrievalService.this.resize) {
					iv.setFitHeight(ImageRetrievalService.this.height);
					iv.setFitWidth(ImageRetrievalService.this.width);
				}
				
				iv.setSmooth(true);
				iv.setCache(true);
				return iv;
			}
		};
	}
}