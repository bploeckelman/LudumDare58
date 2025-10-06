#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_flashback;
uniform float u_time;
uniform float u_fade;
uniform vec2 u_res;

varying vec4 v_color;
varying vec2 v_texCoord;

vec4 desaturate(vec3 color, float factor)
{
    vec3 lum = vec3(0.299, 0.587, 0.114);
    vec3 gray = vec3(dot(lum, color));
    return vec4(mix(color, gray, factor), 1.0);
}


void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord);

    texColor = desaturate(texColor.rgb, u_flashback *  .8);

    vec2 uv = v_texCoord - .5;
    float dist = length(uv);

    float vig = smoothstep(.5, .6, dist);
    float intensity = .6 * u_flashback;
    gl_FragColor = vec4(mix(texColor.rgb, texColor.rgb * (1.0 - intensity), vig), 1.) * v_color;

}
