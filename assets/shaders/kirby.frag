#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_strength;
uniform float u_time;

varying vec4 v_color;
varying vec2 v_texCoord;

float cubicPulse( float c, float w, float x )
{
    x = abs(x - c);
    if( x>w ) return 0.0;
    x /= w;
    return 1.0 - x*x*(3.0-2.0*x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec4 getTexture(vec2 offset) {
    return texture2D(u_texture, v_texCoord + offset);
}

void main() {
    vec4 noise = texture2D(u_texture, v_texCoord);
    vec2 p = (vec2(v_texCoord)-.5) * 2.;
    float dist = length(p);

    float lines = 8.;
    float humps = 6.;
    float degree = atan(p.x, p.y) + u_time * 3.;
    float movedDist = dist + .05 * cos(degree * humps);
    vec4 texColor = vec4(0);
    texColor += mix(vec4(0.),vec4(hsv2rgb(vec3(movedDist+ u_time* 1.4, noise.r, 1.)), .7), cubicPulse(mod(-u_time * 1.,1./lines), .02, mod(movedDist, 1./lines)));

    float alpha = smoothstep(1., .5, dist) * u_strength;
    texColor.a *= alpha;
    gl_FragColor = texColor * v_color;
}
