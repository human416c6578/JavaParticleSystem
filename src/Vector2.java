
public class Vector2 {
    float x;
    float y;

    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void add(Vector2 other, float scale)
    {
        this.x += other.x * scale;
        this.y += other.y * scale;
    }

    public void add(float x, float y)
    {
        this.x += x;
        this.y += y;
    }

    public void add(Vector2 other)
    {
        this.x += other.x;
        this.y += other.y;
    }

    public void multiply(float scale)
    {
        this.x *= scale;
        this.y *= scale;
    }

    public float distance(Vector2 other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        float magnitude = magnitude();
        if (magnitude != 0) {
            this.x /= magnitude;
            this.y /= magnitude;
        }
    }

    public float dot(Vector2 other) {
        return x * other.x + y * other.y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
