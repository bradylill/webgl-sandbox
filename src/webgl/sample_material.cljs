(ns webgl.sample-material)

(def vertex-shader
  "
  uniform float amplitude;
  attribute float displacement;
  varying vec3 vNormal;

  void main ()  {
    vNormal = normal;

    vec3 newPosition = position +
                       vec3(amplitude / displacement);

    gl_Position = projectionMatrix *
                  modelViewMatrix *
                  vec4(newPosition, 1.0);
  }")

(def fragment-shader
  "
  varying vec3 vNormal;

  void main ()  {
    vec3 light = vec3(0.5, 0.2, 1.0);
    light = normalize(light);

    float dProd = max(0.3, dot(vNormal, light));

    gl_FragColor = vec4(1.0, dProd, dProd * 0.5, 1.0);
  }")

(defn attributes [geo]
  (let [values (for [v (.-vertices geo)] (* (rand) 5))]
    {:displacement {:type "f"
                    :value values}}))

(def uniforms {:amplitude {:type "f"
                           :value 0}})

(defn create-material [geo]
  (THREE.ShaderMaterial. (clj->js
                           {:uniforms       uniforms
                            :attributes     (attributes geo)
                            :vertexShader   vertex-shader
                            :fragmentShader fragment-shader})))
