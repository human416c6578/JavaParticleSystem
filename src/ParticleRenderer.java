import static org.lwjgl.opengl.GL46.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class ParticleRenderer {
    private int shader;

    private int quadVAO;
    private int u_ViewProj;
    private int u_Transform;
    private int u_Color;
    private int u_StartLife;
    private int u_Life;

    public void updateProjection(int width, int height) {
        Matrix4f projection = new Matrix4f().ortho2D(0, width, height, 0);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        projection.get(buffer);

        glUseProgram(shader);
        glUniformMatrix4fv(u_ViewProj, false, buffer);
    }

    public ParticleRenderer(int width, int height){
        shader = Shader.createShaderProgram("src/shaders/vertex.glsl", "src/shaders/fragment.glsl");
		glUseProgram(shader);

        // Send the matrix to the shader (make sure uniform location is cached)
        Matrix4f projection = new Matrix4f().ortho2D(0, width, height, 0);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        projection.get(buffer);

        u_ViewProj = glGetUniformLocation(shader, "u_ViewProj");
        u_Transform = glGetUniformLocation(shader, "u_Transform");
        u_Color = glGetUniformLocation(shader, "u_Color");
        u_StartLife = glGetUniformLocation(shader, "u_StartLife");
        u_Life = glGetUniformLocation(shader, "u_Life");

        glUniformMatrix4fv(u_ViewProj, false, buffer);
        initQuad();
    }

    public void initQuad() {
        float[] vertices = {
            // Position           // Texture Coordinates
            -0.5f, -0.5f, 0.0f,   0.0f, 0.0f,   // Bottom-left corner
             0.5f, -0.5f, 0.0f,   1.0f, 0.0f,   // Bottom-right corner
             0.5f,  0.5f, 0.0f,   1.0f, 1.0f,   // Top-right corner
            -0.5f,  0.5f, 0.0f,   0.0f, 1.0f    // Top-left corner
        };

        // Indices for the quad (two triangles)
        int[] indices = {
            0, 1, 2,  // First triangle
            2, 3, 0   // Second triangle
        };

        // Create and bind the VAO
        quadVAO = glGenVertexArrays();
        glBindVertexArray(quadVAO);

        // Create and bind the VBO (Vertex Buffer Object)
        int quadVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Enable vertex attributes (Position and Texture Coordinates)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0); 
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Create and bind the EBO (Element Buffer Object)
        int quadEBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, quadEBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Unbind the buffers
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }


    public void renderParticle(Particle particle, FloatBuffer buffer) {
        Matrix4f transform = new Matrix4f()
            .translate(particle.position.x, particle.position.y, 0f)
            .rotateZ(0.0f) 
            .scale(particle.size);

        transform.get(buffer);

        glUniformMatrix4fv(u_Transform, false, buffer);

        glUniform4f(u_Color, particle.color.r, particle.color.g, particle.color.b, particle.color.a);
        glUniform1f(u_StartLife, particle.startLife);
        glUniform1f(u_Life, particle.life);

        glBindVertexArray(quadVAO);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    
    }
}
