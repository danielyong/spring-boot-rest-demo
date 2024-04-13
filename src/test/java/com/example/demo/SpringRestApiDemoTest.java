package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SpringRestApiDemoTest {
  @Autowired
  private MockMvc mockMvc;

  @SuppressWarnings("unchecked")
  @Test
  public void generateApiDocumentation() throws Exception {
    /*
    String path = servletContext.getContextPath() + apiDocPath + ".yaml";

    MvcResult result = this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk())
        .andReturn();

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    LinkedHashMap<String, Object> raw_yaml = mapper.readValue(result.getResponse().getContentAsString(), LinkedHashMap.class);
    LinkedHashMap<String, Object> raw_paths_yaml = (LinkedHashMap<String, Object>) raw_yaml.get("paths");
    LinkedHashMap<String, Object> fixed_paths_yaml = new LinkedHashMap<>();
  
    Iterator<String> paths_iterator = raw_paths_yaml.keySet().iterator();
    while(paths_iterator.hasNext()){
      String api_path = paths_iterator.next();
      fixed_paths_yaml.put(applicationContextPath + api_path, raw_paths_yaml.get(api_path));
    }
    raw_yaml.put("paths", fixed_paths_yaml);  
    File outputDir = new File("./target");
    outputDir.mkdirs();
    mapper.writeValue(new File(outputDir + "/" + moduleName + "-api-spec.yaml"), raw_yaml);
    */
  }
}
