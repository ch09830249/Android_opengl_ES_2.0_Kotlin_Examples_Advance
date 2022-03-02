precision mediump float;
uniform vec4 lightColor;       //光源色
uniform vec4 objectColor;      //物体⾊    ok 固定

varying vec4 lightPo;          //光源位置
varying vec4 outNormal;        //传入当前顶点平面的法向量
varying vec4 viewPo;           //物体位置
void main()
{
    //確保法線向量為單位向量
    vec4 norm = normalize(outNormal);
    //頂點指向光源 單位向量量
    vec4 lightDir = normalize(lightPo - viewPo);
    //得到兩向量量的cos值 ⼩小於0則則為0
    float diff = max(dot(norm, lightDir),0.05);
    //得到漫反射收的光源向量
    vec4 diffuse = diff * lightColor;
    vec4 result  = diffuse * objectColor;
    gl_FragColor = result;
}