
public class Particle {
    public float size;
    public Vector2 position;
    public Vector2 velocity;
    public float startLife;
    public float life;
    public Color color;
    public float mass;

    public Particle(float size, Vector2 position, Vector2 velocity, Color color, float life, float mass)
    {
        this.size = size;
        this.position = position;
        this.velocity = velocity;
        this.color = color;
        this.startLife = life;
        this.life = life;
        this.mass = mass;
    }

    public void setVelocity(Vector2 velocity)
    {
        this.velocity = velocity;
    }

    public void updatePosition(float deltaTime) {
        life -= deltaTime;
        
        velocity.add(new Vector2(0.0f, 1.0f), 50.0f * deltaTime * mass);
        velocity.multiply(0.99f);
        position.add(velocity, deltaTime);
    }

    public void detectCollisions(int index, Particle[] particles, int particleCount) {
        for (int i = index + 1; i < particleCount; i++) {
            if (this.position.distance(particles[i].position) < ((this.size/2.0) + (particles[i].size/2.0))) {
                // Calculate collision normal
                Vector2 normal = new Vector2(
                    particles[i].position.x - this.position.x,
                    particles[i].position.y - this.position.y
                );
                float distance = normal.magnitude();
                normal.normalize();
    
                // Prevent particles from sticking by adjusting positions
                float overlap = ((this.size/2.0f) + (particles[i].size/2.0f) - distance) / 2;
                this.position.x -= overlap * normal.x;
                this.position.y -= overlap * normal.y;
                particles[i].position.x += overlap * normal.x;
                particles[i].position.y += overlap * normal.y;
    
                // Calculate relative velocity
                Vector2 relativeVelocity = new Vector2(
                    this.velocity.x - particles[i].velocity.x,
                    this.velocity.y - particles[i].velocity.y
                );
    
                // Calculate impulse (simplified using size as mass)
                float impulse = -2 * relativeVelocity.dot(normal) / (this.size + particles[i].size);
                
                // Apply impulse to velocities
                this.velocity.x += impulse * normal.x * particles[i].size;
                this.velocity.y += impulse * normal.y * particles[i].size;
                particles[i].velocity.x -= impulse * normal.x * this.size;
                particles[i].velocity.y -= impulse * normal.y * this.size;
            }
        }
    }
}
