package garndesh.openglrenderer;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Game {
	private static Game instance;
	private ShaderProgram shader;
	private int vaoID, vboID, vboTexID, eboID;
	private Texture texture;
	private Transform transform;
	private Camera camera;

	/**
	 * Create a new Game
	 */
	public Game() {
		try {
			// Set the static reference
			instance = this;

			// Create the default PixelFormat
			PixelFormat pfmt = new PixelFormat();

			// We need a core context with atleast OpenGL 3.2
			ContextAttribs cattr = new ContextAttribs(3, 2)
					.withForwardCompatible(true).withProfileCore(true);

			// Create the Display
			Display.create(pfmt, cattr);
			Display.setResizable(true);
			setDisplayMode(800, 600, false);

			// Start the game
			gameLoop();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1); // There is no point in running without hardware
								// acceleration right?
		}
	}

	/**
	 * Initialize the game
	 */
	public void init() {
		transform = new Transform();

		camera = new Camera(67, ((float)Display.getWidth())/((float)Display.getHeight()), 0.1f, 100);
		camera.setPosition(new Vector3f(0, 0, 0.8f));

		// Create a new ShaderProgram
		shader = new ShaderProgram();
		shader.attachVertexShader("garndesh/openglrenderer/vertex01.vert");
		shader.attachFragmentShader("garndesh/openglrenderer/fragment01.frag");
		shader.link();

		// Vertices for our cube
		FloatBuffer vertices = BufferUtils.createFloatBuffer(24);
		vertices.put(new float[] { -0.5f, +0.5f, +0.5f, // ID: 0
				+0.5f, +0.5f, +0.5f, // ID: 1
				-0.5f, -0.5f, +0.5f, // ID: 2
				+0.5f, -0.5f, +0.5f, // ID: 3
				+0.5f, +0.5f, -0.5f, // ID: 4
				+0.5f, -0.5f, -0.5f, // ID: 5
				-0.5f, +0.5f, -0.5f, // ID: 6
				-0.5f, -0.5f, -0.5f // ID: 7
		});
		vertices.rewind();

		// Colors for our cube
		FloatBuffer colors = BufferUtils.createFloatBuffer(32);
		colors.put(new float[] { 
				1, 0, 0, 0,  	//1
				0, 1, 0, 0,   	//2
				0, 0, 1, 0, 	//3
				1, 1, 1, 0, 	//4
				1, 0, 0, 0, 	//5
				0, 1, 0, 0, 	//6
				0, 0, 1, 0, 	//6
				1, 1, 1, 0 });	//8
		colors.rewind();

		// Elements for our cube
		ShortBuffer elements = BufferUtils.createShortBuffer(36);
		elements.put(new short[] { 
				0, 1, 2, 2, 3, 1, // Front face
				1, 4, 3, 3, 5, 4, // Right face
				4, 6, 5, 5, 7, 6, // Back face
				6, 0, 7, 7, 2, 0, // Left face
				6, 4, 0, 0, 1, 4, // Top face
				7, 5, 2, 2, 3, 5 // Bottom face
		});
		elements.rewind();

		// Create a VAO
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		// Create a VBO
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Create a VBO for the colors
		vboTexID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboTexID);
		glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Create a EBO for indexed drawing
		eboID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);

		// Unbind the VAO
		glBindVertexArray(0);

		glEnable(GL_DEPTH_TEST);
		/*
		 * texture = Texture
		 * .loadTexture("garndesh/openglrenderer/textures/tut_texture.png");
		 * texture.setActiveTextureUnit(0); texture.bind();
		 */
	}

	public static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	private void gameLoop() {
		long lastFrame = getCurrentTime();
		long thisFrame = getCurrentTime();

		init();

		while (!Display.isCloseRequested()) {
			thisFrame = getCurrentTime();
			update(thisFrame - lastFrame);
			render();

			lastFrame = thisFrame;
			Display.update();

			// Check the resize property
			if (Display.wasResized())
				resized();

			Display.sync(60); // You can write this value to a variable to
								// change it.
		}

		end();
	}

	/**
	 * Update the game
	 */
	public void update(long elapsedTime) {
		//transform.rotate(1, 1, 1);
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			Game.end();
		
		// Look up
		//if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			camera.rotateX(Mouse.getDX());
		// Look down
		//if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			//camera.rotateX(-1);
		// Turn left
		//if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			camera.rotateY(Mouse.getDY());
		// Turn right
		//if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			//camera.rotateY(-1);
		// Move front
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			camera.move(Camera.Direction.FORWARD, 0.05f);
		// Move back
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
			camera.move(Camera.Direction.BACKWARD, 0.05f);
		// Strafe left
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			camera.move(Camera.Direction.LEFT, 0.05f);
		// Strafe right
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			camera.move(Camera.Direction.RIGHT, 0.05f);
		// Move up
		if (Keyboard.isKeyDown(Keyboard.KEY_Z))
			camera.move(Camera.Direction.UP, 0.05f);
		// Move down
		if (Keyboard.isKeyDown(Keyboard.KEY_X))
			camera.move(Camera.Direction.DOWN, 0.05f);
		// Update the Camera
		camera.update();
	}

	/**
	 * Render the game
	 */
	public void render() {
		// Clear the screen and depth buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// Move the entire scene left by one
		transform.translate(-1, 0, 0);
		// Draw an array of cubes
		for (int x = 0; x < 2; x++) {
			// Move the column of cubes
			transform.translate(x, 0, 0);
			for (int z = 0; z < 5; z++) {
				// Add some depth for each row
				transform.translate(0, 0, -2);
				// Bind the shaders
				shader.bind();
				shader.setUniform("m_model", transform.getFloatBuffer());
				shader.setUniform("m_view", camera.getViewBuffer());
				shader.setUniform("m_proj", camera.getProjectionBuffer());
				// Bind the VAO
				glBindVertexArray(vaoID);
				glEnableVertexAttribArray(0);
				glEnableVertexAttribArray(1);
				// Draw a cube
				glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_SHORT, 0);
				// Unbind the VAO
				glDisableVertexAttribArray(0);
				glDisableVertexAttribArray(1);
				glBindVertexArray(0);
				// Unbind the shaders
				ShaderProgram.unbind();
			}
			transform.reset();
		}
	}

	/**
	 * Handle Display resizing
	 */
	public void resized() {
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	/**
	 * Sets a DisplayMode after selecting for a better one.
	 * 
	 * @param width
	 *            The width of the display.
	 * @param height
	 *            The height of the display.
	 * @param fullscreen
	 *            The fullscreen mode.
	 *
	 * @return True if switching is successful. Else false.
	 */
	public static boolean setDisplayMode(int width, int height,
			boolean fullscreen) {
		// return if requested DisplayMode is already set
		if ((Display.getDisplayMode().getWidth() == width)
				&& (Display.getDisplayMode().getHeight() == height)
				&& (Display.isFullscreen() == fullscreen))
			return true;

		try {
			// The target DisplayMode
			DisplayMode targetDisplayMode = null;

			if (fullscreen) {
				// Gather all the DisplayModes available at fullscreen
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				// Iterate through all of them
				for (DisplayMode current : modes) {
					// Make sure that the width and height matches
					if ((current.getWidth() == width)
							&& (current.getHeight() == height)) {
						// Select the one with greater frequency
						if ((targetDisplayMode == null)
								|| (current.getFrequency() >= freq)) {
							// Select the one with greater bits per pixel
							if ((targetDisplayMode == null)
									|| (current.getBitsPerPixel() > targetDisplayMode
											.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequency against
						// the
						// original display mode then it's probably best to go
						// for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display
								.getDesktopDisplayMode().getBitsPerPixel())
								&& (current.getFrequency() == Display
										.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				// No need to query for windowed mode
				targetDisplayMode = new DisplayMode(width, height);
			}

			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: " + width + "x"
						+ height + " fs=" + fullscreen);
				return false;
			}

			// Set the DisplayMode we've found
			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

			System.out.println("Selected DisplayMode: "
					+ targetDisplayMode.toString());

			// Generate a resized event
			instance.resized();

			return true;
		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode " + width + "x" + height
					+ " fullscreen=" + fullscreen + e);
		}

		return false;
	}

	/**
	 * Dispose the game
	 */
	public void dispose() {
		// Dispose the shaders
		shader.dispose();

		// Dispose the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoID);

		// Dispose the VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboID);
	}

	public static void end() {
		instance.dispose();
		Display.destroy();
		System.exit(0);
	}
}
