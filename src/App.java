
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.*;

public class App {
	private long window;
    int WIDTH = 800;
    int HEIGHT = 800;
	boolean mousePressed = false;
	float lastMouseX = 0;
	float lastMouseY = 0;

    ParticleSystem particleSystem;
	ParticleSpawner particleSpawner;

	public void run() {
		System.out.println("LWJGL Version: " + Version.getVersion());
        particleSystem = new ParticleSystem(100_000, WIDTH, HEIGHT);
		particleSpawner = new ParticleSpawner(particleSystem, WIDTH, HEIGHT);

		init();
		loop();

		particleSystem.shutdown();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Particle System!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(windowHandle, true);
		});

		glfwSetWindowSizeCallback(window, (window, width, height) -> {
            WIDTH = width;
            HEIGHT = height;
            glViewport(0, 0, width, height);
			particleSystem.updateDimensions(width, height);
            particleSpawner.updateDimensions(width, height);
        });

		glfwSetFramebufferSizeCallback(window, (win, width, height) -> {
			WIDTH = width;
            HEIGHT = height;
			glViewport(0, 0, width, height);
			particleSystem.updateDimensions(width, height);
			particleSpawner.updateDimensions(width, height);
		});

		particleSpawner.setupMouseButtonCallback(window);
		particleSpawner.setupMouseMoveCallback(window);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(0);

		// Make the window visible
		glfwShowWindow(window);
		GL.createCapabilities();

		particleSystem.initRenderer();
	}

	private void loop() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        Float deltaTime = 0.0f;
        long lastTime = System.nanoTime();

        Float printTime = 0.0f;
        int frames = 0;

		while ( !glfwWindowShouldClose(window) ) {
            long currentTime = System.nanoTime();
            deltaTime = (currentTime - lastTime) / 1_000_000_000.0f; // convert nanoseconds to seconds
            lastTime = currentTime;
            printTime+=deltaTime;

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
            particleSystem.update(deltaTime);
			particleSystem.render();
            if(printTime > 1.0f)
            {
				glfwSetWindowTitle(window, "FPS: " + frames + " | Num Particles: " + particleSystem.particleCount);
                printTime = 0.0f;
                frames = 0;
            }

            frames++;
            
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	public static void main(String[] args) {
		new App().run();
	}
}
