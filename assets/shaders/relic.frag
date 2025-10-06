#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_rotation;

varying vec4 v_color;
varying vec2 v_texCoord;


vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec2 p = (v_texCoord - .5) * 2.;

    float r = degrees(atan(p.y, p.x));
    vec4 texColor = vec4(hsv2rgb(vec3((r- u_rotation * 20.)/360., .2, .8)), 1.);

    float a = r + u_rotation * 15.;
    texColor.rgb = mix(vec3(0), texColor.rgb, smoothstep(22., 23., mod(a, 45.)));

    gl_FragColor = texColor * v_color;

}
