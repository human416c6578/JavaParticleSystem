
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

import org.lwjgl.BufferUtils;

public class ParticleSystem {
    private int WIDTH;
    private int HEIGHT;

    private Particle[] particles;
    private boolean[] particleEmpty;
    public int particleCount = 0;
    private ExecutorService executor;
    private int numThreads;
    private ParticleRenderer particleRenderer;
    
    void updateDimensions(int width, int height){
        this.WIDTH = width;
        this.HEIGHT = height;
        particleRenderer.updateProjection(width, height);
    }

    public ParticleSystem(int maxParticles, int width, int height) {
        particles = new Particle[maxParticles];
        particleEmpty = new boolean[maxParticles];
        Arrays.fill(particleEmpty, true);

        this.WIDTH = width;
        this.HEIGHT = height;

        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        numThreads = Runtime.getRuntime().availableProcessors();
    }
    
    public void initRenderer()
    {
        particleRenderer = new ParticleRenderer(WIDTH, HEIGHT);
    }
    
    public void update(float deltaTime) {
        if (particleCount == 0) return;
        
        int chunkSize = Math.max(particleCount / numThreads, 100);
        int taskCount = (int)Math.ceil((double)particleCount / chunkSize);
    
        
        waitForPhase(taskCount, chunkSize, i -> {
            if (!particleEmpty[i]) {
                particles[i].detectCollisions(i, particles, particleCount);
            }
        });
         
        waitForPhase(taskCount, chunkSize, i -> {
            if (!particleEmpty[i]) {
                particles[i].updatePosition(deltaTime);
            }
        });
        removeDeadParticles();
    }

    public void render() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        for (int i = 0; i < particleCount; i++) {
            if (particleEmpty[i]) continue;

            particleRenderer.renderParticle(particles[i], buffer);
        }
    }

    private void waitForPhase(int taskCount, int chunkSize, IntConsumer taskExecutor) {
        Phaser phase = new Phaser(1); // main thread
        phase.bulkRegister(taskCount);

        for (int start = 0; start < particleCount; start += chunkSize) {
            final int from = start;
            final int to = Math.min(start + chunkSize, particleCount);
            executor.execute(() -> {
                try {
                    for (int i = from; i < to; i++) {
                        taskExecutor.accept(i);
                    }
                } finally {
                    phase.arrive();
                }
            });
        }

        phase.arriveAndAwaitAdvance();
    }
        
    private void removeDeadParticles() {
        int i = 0;
        for (int j = 0; j < particleCount; j++) {
            Vector2 pos = particles[j].position;
            Vector2 vel = particles[j].velocity;
    
            if (pos.y > HEIGHT) {
                vel.y *= -1.0f;
                pos.y = Math.max(0.0f, Math.min(pos.y, HEIGHT));

                particles[i++] = particles[j];
                continue;
            }
    
            // Only keep alive particles
            if (particles[j].life > 0.0f && insideBounds(particles[j])) {
                particles[i++] = particles[j];
            }
        }
        particleCount = i;
        for (int k = particleCount; k < particles.length; k++) {
            particles[k] = null;
            particleEmpty[k] = true;
        }
    }

    private boolean insideBounds(Particle particle)
    {
        return particle.position.x > 0 && particle.position.y > 0 && particle.position.x < WIDTH && particle.position.y < HEIGHT;
    }
    
    // Clean up resources
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    public void spawn(Particle p) {
        if (particleCount < particles.length) {
            particleEmpty[particleCount] = false;
            particles[particleCount++] = p;
            
        } else {
            return;
            //throw new IllegalStateException("Particle array is full");
        }
    }

}
