#version 460 core
out vec4 FragColor;

uniform vec4 u_Color;
uniform float u_StartLife;
uniform float u_Life;

void main() {
    float lifePercent = clamp(u_Life / u_StartLife, 0.0, 1.0);

    vec4 yellow = vec4(1.0, 0.6, 0.0, 1.0);
    vec4 red    = vec4(1.0, 0.0, 0.0, 0.0); 

    float t = lifePercent / 0.7;
    FragColor = mix(red, yellow, t);
}
