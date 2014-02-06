/**
 * Copyright 2014 the staff of 52°North Initiative for Geospatial Open
 * Source Software GmbH in their free time
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spare.n52.yadarts;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.splash.Splash;

public class YadartsDesktopMain {
	
	private static final Logger logger = LoggerFactory.getLogger(YadartsDesktopMain.class);

	public static void main(String[] args) {
		logger.info("bootstrapping yadarts desktop...");
		final Display display = Display.getDefault();
		new Splash(display, new Splash.SplashListener() {

			@Override
			public void onSplashFinished(Splash s) {
				startMainApp(display, s);
			}
		});
	}

	protected static void startMainApp(Display display, final Splash splash) {
		new MainWindow(display, new MainWindow.MainWindowOpenedListener() {
			
			@Override
			public void onMainWindowOpened() {
				splash.closeSelf();
			}
		});
	}

}
