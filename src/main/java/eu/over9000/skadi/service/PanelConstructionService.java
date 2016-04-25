package eu.over9000.skadi.service;

import eu.over9000.skadi.model.Panel;
import eu.over9000.skadi.util.PanelUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.layout.VBox;

/**
 * Created by Jan on 25.04.2016.
 */
public class PanelConstructionService extends Service<VBox> {
	private Panel panel;

	public PanelConstructionService(Panel panel) {
		this.panel = panel;
	}

	@Override
	protected Task<VBox> createTask() {
		return new Task<VBox>() {
			@Override
			protected VBox call() throws Exception {
				return PanelUtil.buildPanel(panel);
			}
		};
	}
}
