#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_percent;

varying vec4 v_color;
varying vec2 v_texCoord;


void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord) ;
    float dim = 1. - (smoothstep(u_percent, u_percent-.04, v_texCoord.x) * .7);

    texColor.rgb *= dim;
    gl_FragColor = texColor * v_color;

}
