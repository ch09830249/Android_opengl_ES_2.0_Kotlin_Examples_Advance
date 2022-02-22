precision mediump float;

//紋理的具體資料 1
uniform sampler2D u_TextureUnit;

//紋理的具體資料 2
uniform sampler2D u_TextureUnit1;

//紋理座標st
varying vec2 v_TextureCoordinates;

void main() {
    //texture2D:根據紋理座標st，取出具體的顏色值
    gl_FragColor = texture2D(u_TextureUnit1,v_TextureCoordinates) * texture2D(u_TextureUnit,v_TextureCoordinates);
}