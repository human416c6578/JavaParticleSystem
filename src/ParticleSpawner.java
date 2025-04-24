import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.*;

import java.nio.DoubleBuffer;
import java.util.Random;

public class ParticleSpawner {
    private int WIDTH;
    private int HEIGHT;

    private ParticleSystem particleSystem;
    private boolean mousePressed = false;
    private float lastMouseX = 0.0f;
    private float lastMouseY = 0.0f;

    void updateDimensions(int width, int height){
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    public ParticleSpawner(ParticleSystem particleSystem, int width, int height){
        this.particleSystem = particleSystem;
        this.WIDTH = width;
        this.HEIGHT = height;
        //spawnParticles(10000);
    }

    public void setupMouseButtonCallback(long window) {
        glfwSetMouseButtonCallback(window, (windowHandle, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                if (action == GLFW_PRESS) {
                    // When pressed, update state and record position
                    mousePressed = true;

                    DoubleBuffer posx = BufferUtils.createDoubleBuffer(1);
                    DoubleBuffer posy = BufferUtils.createDoubleBuffer(1);
                    glfwGetCursorPos(windowHandle, posx, posy);
                    posx.rewind();
                    posy.rewind();

                    lastMouseX = (float) posx.get();
                    lastMouseY = (float) posy.get();
                } else if (action == GLFW_RELEASE) {
                    // When released, update state
                    mousePressed = false;
                }
            }
        });
    }

    public void setupMouseMoveCallback(long window) {
        glfwSetCursorPosCallback(window, (windowHandle, xpos, ypos) -> {
            if (mousePressed) {
                // Calculate mouse movement delta
                float deltaX = (float) xpos - lastMouseX;
                float deltaY = (float) ypos - lastMouseY;
                float dragSpeed = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                // Only spawn particles if mouse has moved enough
                if (dragSpeed > 2.0f) {
                    // Create particles along the drag path
                    Vector2 position = new Vector2((float) xpos, (float) ypos);

                    float speed = (float) Math.random() * 25.0f + 25.0f;
                    Vector2 velocity = new Vector2(deltaX, deltaY);
                    velocity.normalize();
                    velocity.add(velocity, speed);

                    // Spawn particles with velocity opposite to drag direction
                    int particleCount = (int) (dragSpeed / 2) + 150;
                    for (int i = 0; i < particleCount; i++) {
                        // Add some randomness
                        float offsetX = (float) (Math.random() * 6 - 3);
                        float offsetY = (float) (Math.random() * 6 - 3);
                        Vector2 particlePos = new Vector2(position.x + offsetX, position.y + offsetY);

                        // Add slight randomness to velocity
                        float angleVariation = (float) (Math.random() * 1.0 - 0.5); // ±0.5 radians
                        float newAngle = (float) Math.atan2(velocity.y, velocity.x) + angleVariation;
                        float velX = (float) (Math.cos(newAngle) * speed * (1.0f + Math.random() * 1.5f));
                        float velY = (float) (Math.sin(newAngle) * speed * (1.0f + Math.random() * 1.5f));
                        Vector2 particleVel = new Vector2(velX, velY);

                        spawnParticle(particlePos, particleVel, (float) (1.0f + Math.random() * 1.5f));
                    }

                    // Update last position
                    lastMouseX = (float) xpos;
                    lastMouseY = (float) ypos;
                }
            }
        });
    }

    private void spawnParticles(int num)
	{
		Random rnd = new Random();
        float side_speed = 20.0f;
		float fall_speed = 100.0f;
        int max_size = 4;
        for(int i =0;i<num;i++)
        {
            Color c = new Color(rnd.nextFloat(0.1f), rnd.nextFloat(0.1f), rnd.nextFloat(1.0f));
            float x = rnd.nextFloat(WIDTH);
            Vector2 position = new Vector2(x, rnd.nextFloat(HEIGHT));
			float size = rnd.nextFloat(max_size) + 1;
            Vector2 velocity = new Vector2(side_speed * size, fall_speed * size);
            float life = rnd.nextFloat(99);
			
            Particle p = new Particle(size, position, velocity, c, life);
            particleSystem.spawn(p);
        }
	}

	private void spawnParticle(Vector2 position, Vector2 velocity, float life)
	{
		Random rnd = new Random();
        int max_size = 2;

        Color c = new Color(rnd.nextFloat(0.1f), rnd.nextFloat(0.4f), rnd.nextFloat(0.8f));
        Particle p = new Particle(max_size, position, velocity, c, life);
        particleSystem.spawn(p);

	}

}
