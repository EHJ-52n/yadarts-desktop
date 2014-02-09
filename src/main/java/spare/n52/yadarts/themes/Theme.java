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
package spare.n52.yadarts.themes;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A theme is a set of image files that provide the
 * basic style of the UI.
 */
public class Theme {
	
	private static final Logger logger = LoggerFactory.getLogger(Theme.class);
	
	private static final String BOARD_HI = "board-hi.jpg";
	private static final String BOARD_M = "board-m.jpg";
	private static final String BOARD_LO = "board-lo.jpg";
	private static final String BASE_DIR = "/themes";
	private static final String DEFAULT_THEME = "plain";
	private static final String BACKGROUND = "background.jpg";
	private static final String BACKGROUND_ALT = "background_alt.jpg";
	private static final String CORNER_TOP_LEFT = "corner_topleft.jpg";
	private static final String BORDER_LEFT = "border_left.jpg";
	
	private static Theme defaultTheme;
	private static Map<String, Theme> availableThemes = new HashMap<>();
	private static Theme currentTheme;
	
	static {
		try {
			defaultTheme = new Theme(BASE_DIR + "/"+ DEFAULT_THEME);
			currentTheme = defaultTheme;
			resolveThemes();
		} catch (IOException | URISyntaxException e) {
			logger.warn(e.getMessage(), e);
		}
	}

	public static Theme getDefault() {
		return defaultTheme;
	}
	
	public static void setCurrentTheme(String name) {
		if (availableThemes.containsKey(name)) {
			currentTheme = availableThemes.get(name);
		}
		else {
			logger.warn("No theme with name {} available. Using default", name);
		}
	}

	public static Theme getCurrentTheme() {
		return currentTheme;
	}
	
	private static void resolveThemes() throws IOException, URISyntaxException {
		URL baseDirUrl = Theme.class.getResource(BASE_DIR);
		File baseDir = new File(baseDirUrl.toURI());
		
		if (baseDir.exists() && baseDir.isDirectory()) {
			File[] candidates = baseDir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					/*
					 * use directories, but not the default. we already have that
					 */
					if (pathname.isDirectory() && !pathname.equals(DEFAULT_THEME)) {
						return true;
					}
					return false;
				}
			});
			
			instantiateThemes(candidates);
		}
	}

	private static void instantiateThemes(File[] candidates) {
		for (File c : candidates) {
			File[] contents = c.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return validateDirectoryContents(pathname);
				}

			});
			
			if (contents.length == 3) {
				try {
					availableThemes.put(c.getName(), new Theme(c));
				}
				catch (IllegalStateException e) {
					logger.warn("Could not instantiate theme {}: {}", c.getName(), e.getMessage());
				}
			}
		}
	}
	
	private static boolean validateDirectoryContents(File pathname) {
		boolean hasHi = false;
		boolean hasM = false;
		boolean hasLo = false;
		
		switch (pathname.getName()) {
		case BOARD_HI:
			hasHi = true;
			break;
		case BOARD_M:
			hasM = true;
			break;
		case BOARD_LO:
			hasLo = true;
			break;
		default:
			break;
		}
		
		return hasHi && hasM && hasLo;
	}

	private File baseDir;
	private File boardHiFile;
	private File boardMFile;
	private File boardLoFile;
	private File backgroundImageFile;
	private File backgroundAltImageFile;
	private File cornerTopLeftImageFile;
	private File borderLeftImageFile;
	
	private Image boardHiImage;
	private Image boardMImage;
	private Image boardLoImage;
	private Image backgroundImage;

	private Image backgroundAltImage;

	private Image cornerTopLeftImage;

	private Image borderLeftImage;



	public Theme(String path) throws URISyntaxException {
		this(new File(Theme.class.getResource(path).toURI()));
	}

	public Theme(File c) {
		this.baseDir = c;
		resolveImageFiles();
	}

	private void resolveImageFiles() {
		this.boardHiFile = new File(baseDir, BOARD_HI);
		this.boardMFile = new File(baseDir, BOARD_M);
		this.boardLoFile = new File(baseDir, BOARD_LO);
		this.backgroundImageFile = new File(baseDir, BACKGROUND);
		this.backgroundAltImageFile = new File(baseDir, BACKGROUND_ALT);
		this.cornerTopLeftImageFile = new File(baseDir, CORNER_TOP_LEFT);
		this.borderLeftImageFile = new File(baseDir, BORDER_LEFT);
		
		assertFilesExist();
	}
	
	private void assertFilesExist() {
		assertFileExists(this.boardHiFile);
		assertFileExists(this.boardMFile);
		assertFileExists(this.boardLoFile);
		assertFileExists(this.backgroundImageFile);
		assertFileExists(this.backgroundAltImageFile);
		assertFileExists(this.cornerTopLeftImageFile);
		assertFileExists(this.borderLeftImageFile);
	}

	private void assertFileExists(File f) {
		if (f == null || !f.exists() || f.isDirectory()) {
			throw new IllegalStateException("Missing theme file.");
		}
	}

	/**
	 * the hi-res version of the dart board
	 */
	public synchronized Image getBoardHi(Device d) throws FileNotFoundException {
		if (this.boardHiImage == null) {
			this.boardHiImage = new Image(d, new FileInputStream(boardHiFile));
		}
		
		return this.boardHiImage;
	}
	
	/**
	 * the med-res version of the dart board
	 */
	public synchronized Image getBoardM(Device d) throws FileNotFoundException {
		if (this.boardMImage == null) {
			this.boardMImage = new Image(d, new FileInputStream(boardMFile));
		}
		
		return this.boardMImage;
	}
	
	/**
	 * the lo-res version of the dart board
	 */
	public synchronized Image getBoardLo(Device d) throws FileNotFoundException {
		if (this.boardLoImage == null) {
			this.boardLoImage = new Image(d, new FileInputStream(boardLoFile));
		}
		
		return this.boardLoImage;
	}
	
	public synchronized Image getBackground(Device d) throws FileNotFoundException {
		if (this.backgroundImage == null) {
			this.backgroundImage = new Image(d, new FileInputStream(backgroundImageFile));
		}
		
		return this.backgroundImage;
	}
	
	public synchronized Image getBackgroundAlt(Device d) throws FileNotFoundException {
		if (this.backgroundAltImage == null) {
			this.backgroundAltImage = new Image(d, new FileInputStream(backgroundAltImageFile));
		}
		
		return this.backgroundAltImage;
	}
	
	public synchronized Image getCornerTopLeft(Device d) throws FileNotFoundException {
		if (this.cornerTopLeftImage == null) {
			this.cornerTopLeftImage = new Image(d, new FileInputStream(cornerTopLeftImageFile));
		}
		
		return this.cornerTopLeftImage;
	}
	
	public synchronized Image getBorderLeft(Device d) throws FileNotFoundException {
		if (this.borderLeftImage == null) {
			this.borderLeftImage = new Image(d, new FileInputStream(borderLeftImageFile));
		}
		
		return this.borderLeftImage;
	}

	public File getBoardHiFile() {
		return checkAndReturnExistingFile(this.boardHiFile);
	}

	public File getBoardMFile() {
		return checkAndReturnExistingFile(boardMFile);
	}

	public File getBoardLoFile() {
		return checkAndReturnExistingFile(boardLoFile);
	}
	
	private File checkAndReturnExistingFile(File f) {
		if (f != null && f.exists() && !f.isDirectory()) {
			return f;
		}
		return null;
	}
	
}
