#version 330 core

uniform sampler2D tex;

in vec4 color;
out vec4 outColor;

void main()
{
    outColor = color;
}