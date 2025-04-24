import static org.lwjgl.opengl.GL46.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Shader {

    public static int createShaderProgram(String vertPath, String fragPath) {
        String vertSrc = loadShaderSource(vertPath);
        String fragSrc = loadShaderSource(fragPath);

        int vertShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertShader, vertSrc);
        glCompileShader(vertShader);
        checkShaderCompile(vertShader);

        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShader, fragSrc);
        glCompileShader(fragShader);
        checkShaderCompile(fragShader);

        int program = glCreateProgram();
        glAttachShader(program, vertShader);
        glAttachShader(program, fragShader);
        glLinkProgram(program);
        checkProgramLink(program);

        glDeleteShader(vertShader);
        glDeleteShader(fragShader);

        return program;
    }

    private static void checkShaderCompile(int shader) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader compile failed:\n" + glGetShaderInfoLog(shader));
        }
    }

    private static void checkProgramLink(int program) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Program link failed:\n" + glGetProgramInfoLog(program));
        }
    }

    private static String loadShaderSource(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shader file: " + filePath, e);
        }
    }
}

