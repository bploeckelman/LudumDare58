#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform sampler2D u_texture2;
uniform float u_time;
uniform vec3 u_res;
uniform float u_strength;

varying vec4 v_color;
varying vec2 v_texCoord;

// All components are in the range [0…1], including hue.
vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}


// All components are in the range [0…1], including hue.
vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

// Take something [0,1] and make it [-1,1]
float n(float x) {
    return x * 2. -1.;
}

void main() {
    vec2 np = vec2(v_texCoord.x + (u_time/10.5), v_texCoord.y + (u_time/10.5));
    vec2 np2 = vec2(v_texCoord.x - (u_time/10.), v_texCoord.y + (u_time/10.));
    vec2 np3 = vec2(v_texCoord.x - (u_time/9.), v_texCoord.y - (u_time/9.));
    vec2 np4 = vec2(v_texCoord.x + (u_time/8.), v_texCoord.y - (u_time/8.));
    vec4 noise = texture2D(u_texture2, np) + texture2D(u_texture2, np2) +texture2D(u_texture2, np3) + texture2D(u_texture2, np4) ;
    noise /= 4.;

    vec2 offset = vec2(n(noise.r), n(noise.g)) * .05;
    offset *= u_strength;
    vec4 texColor = texture2D(u_texture, v_texCoord + offset);
    vec3 hsv = rgb2hsv(texColor.rgb);
    hsv.y *= (1. + (u_strength* 1.5)); // y == Saturdation
    hsv.x += n(noise.r) * .5 * u_strength;  // x = Hue
    vec3 finalColor = hsv2rgb(hsv);

    gl_FragColor = vec4(finalColor, 1.) * v_color;

//    gl_FragColor = vec4(vec3(noise.r),1.);
}
