package org.example.ScalaAkkaSpringTryout.web

import org.springframework.web.bind.annotation.{GetMapping, RestController}

@RestController
class HelloController {

  @GetMapping(path = Array("/hello"))
  def hello: String = "Hello"
}
