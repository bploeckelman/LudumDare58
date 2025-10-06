#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform vec4 u_outline_color;
uniform vec2 u_thickness;
uniform vec4 u_fill_color;

varying vec4 v_color;
varying vec2 v_texCoord;



vec4 getTexture(vec2 offset) {
    return texture2D(u_texture, v_texCoord + offset);
}

void main() {
    vec4 texColor = getTexture(vec2(0.));
    float outline = 0.;
    vec2 p = vec2(v_texCoord);
    if (texColor.a <= 0.05) {
        outline += getTexture(vec2(u_thickness.x, u_thickness.y)).a;
        outline += getTexture(vec2(u_thickness.x, -u_thickness.y)).a;
        outline += getTexture(vec2(-u_thickness.x, -u_thickness.y)).a;
        outline += getTexture(vec2(-u_thickness.x, u_thickness.y)).a;
        outline += getTexture(vec2(0., u_thickness.y)).a;
        outline += getTexture(vec2(0., -u_thickness.y)).a;
        outline += getTexture(vec2(u_thickness.x, 0.)).a;
        outline += getTexture(vec2(-u_thickness.x, 0.)).a;
        outline = clamp(outline, 0., 1.);
    }
    texColor = mix(texColor, u_fill_color, u_fill_color.a * texColor.a);

    texColor = mix(texColor, u_outline_color, outline);

    gl_FragColor = texColor * v_color;
}
