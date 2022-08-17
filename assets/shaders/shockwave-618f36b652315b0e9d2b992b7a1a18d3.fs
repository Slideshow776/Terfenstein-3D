#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D sceneTex; // 0
uniform vec2 u_center; // Mouse position
uniform float u_time; // effect elapsed time
uniform vec3 u_shockParams; // 10.0, 0.8, 0.1

varying vec2 v_texCoords;

void main()
{
	// get pixel coordinates
	vec2 l_texCoords = v_texCoords;

	//get distance from center
	float distance = distance(v_texCoords, u_center);

	if ( (distance <= (u_time + u_shockParams.z)) && (distance >= (u_time - u_shockParams.z)) ) {
    	float diff = (distance - u_time);
    	float powDiff = 1.0 - pow(abs(diff*u_shockParams.x), u_shockParams.y);
    	float diffTime = diff  * powDiff;
    	vec2 diffUV = normalize(v_texCoords-u_center);
    	l_texCoords = v_texCoords + (diffUV * diffTime);
	}
	gl_FragColor = texture2D(sceneTex, l_texCoords);
}
