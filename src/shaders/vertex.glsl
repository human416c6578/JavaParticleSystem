#version 330 core

layout(location = 0) in vec3 a_Pos; 
layout(location = 1) in vec2 a_TexCoord;

uniform mat4 u_ViewProj;
uniform mat4 u_Transform;
uniform vec4 u_Color;
uniform float u_StartLife;
uniform float u_Life;

out vec2 TexCoord;

void main() {
    float lifePercent = clamp(u_Life / u_StartLife, 0.0, 1.0);

    float scale = mix(0.5, 1.5, lifePercent);
    vec3 scaledPos = a_Pos * scale;

    gl_Position = u_ViewProj * u_Transform * vec4(scaledPos, 1.0f);
    TexCoord = a_TexCoord;
}
