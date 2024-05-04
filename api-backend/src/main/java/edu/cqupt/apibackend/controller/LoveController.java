package edu.cqupt.apibackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoveController {

	@GetMapping("/love")
	public String hello() {
		return "<!DOCTYPE html>\n" +
				"<html>\n" +
				"  <head>\n" +
				"    <meta charset=\"utf-8\" />\n" +
				"    <title>\uD83D\uDC97</title>\n" +
				" \n" +
				"    <style>\n" +
				"      html,\n" +
				"      body {\n" +
				"        height: 100%;\n" +
				"        padding: 0;\n" +
				"        margin: 0;\n" +
				"        background: #000;\n" +
				"      }\n" +
				"      canvas {\n" +
				"        position: absolute;\n" +
				"        width: 100%;\n" +
				"        height: 100%;\n" +
				"        animation: anim 1.5s ease-in-out infinite;\n" +
				"        -webkit-animation: anim 1.5s ease-in-out infinite;\n" +
				"        -o-animation: anim 1.5s ease-in-out infinite;\n" +
				"        -moz-animation: anim 1.5s ease-in-out infinite;\n" +
				"      }\n" +
				"      #name {\n" +
				"        position: absolute;\n" +
				"        top: 50%;\n" +
				"        left: 50%;\n" +
				"        transform: translate(-50%, -50%);\n" +
				"        margin-top: -20px;\n" +
				"        font-size: 46px;\n" +
				"        color: #ea80b0;\n" +
				"      }\n" +
				"      @keyframes anim {\n" +
				"        0% {\n" +
				"          transform: scale(0.8);\n" +
				"        }\n" +
				"        25% {\n" +
				"          transform: scale(0.7);\n" +
				"        }\n" +
				"        50% {\n" +
				"          transform: scale(1);\n" +
				"        }\n" +
				"        75% {\n" +
				"          transform: scale(0.7);\n" +
				"        }\n" +
				"        100% {\n" +
				"          transform: scale(0.8);\n" +
				"        }\n" +
				"      }\n" +
				"      @-webkit-keyframes anim {\n" +
				"        0% {\n" +
				"          -webkit-transform: scale(0.8);\n" +
				"        }\n" +
				"        25% {\n" +
				"          -webkit-transform: scale(0.7);\n" +
				"        }\n" +
				"        50% {\n" +
				"          -webkit-transform: scale(1);\n" +
				"        }\n" +
				"        75% {\n" +
				"          -webkit-transform: scale(0.7);\n" +
				"        }\n" +
				"        100% {\n" +
				"          -webkit-transform: scale(0.8);\n" +
				"        }\n" +
				"      }\n" +
				"      @-o-keyframes anim {\n" +
				"        0% {\n" +
				"          -o-transform: scale(0.8);\n" +
				"        }\n" +
				"        25% {\n" +
				"          -o-transform: scale(0.7);\n" +
				"        }\n" +
				"        50% {\n" +
				"          -o-transform: scale(1);\n" +
				"        }\n" +
				"        75% {\n" +
				"          -o-transform: scale(0.7);\n" +
				"        }\n" +
				"        100% {\n" +
				"          -o-transform: scale(0.8);\n" +
				"        }\n" +
				"      }\n" +
				"      @-moz-keyframes anim {\n" +
				"        0% {\n" +
				"          -moz-transform: scale(0.8);\n" +
				"        }\n" +
				"        25% {\n" +
				"          -moz-transform: scale(0.7);\n" +
				"        }\n" +
				"        50% {\n" +
				"          -moz-transform: scale(1);\n" +
				"        }\n" +
				"        75% {\n" +
				"          -moz-transform: scale(0.7);\n" +
				"        }\n" +
				"        100% {\n" +
				"          -moz-transform: scale(0.8);\n" +
				"        }\n" +
				"      }\n" +
				"    </style>\n" +
				"  </head>\n" +
				"  <body>\n" +
				"    <canvas id=\"pinkboard\"></canvas>\n" +
				"    <!-- 在下面加名字 -->\n" +
				"     <div id=\"name\" style=\"color: pink;\">王燕希</div> \n" +
				" \n" +
				"    <script>\n" +
				"      var settings = {\n" +
				"        particles: {\n" +
				"          length: 500, \n" +
				"          duration: 2, \n" +
				"          velocity: 100, \n" +
				"          effect: -0.75,\n" +
				"          size: 30, \n" +
				"        },\n" +
				"      };\n" +
				"      (function () {\n" +
				"        var b = 0;\n" +
				"        var c = [\"ms\", \"moz\", \"webkit\", \"o\"];\n" +
				"        for (var a = 0; a < c.length && !window.requestAnimationFrame; ++a) {\n" +
				"          window.requestAnimationFrame = window[c[a] + \"RequestAnimationFrame\"];\n" +
				"          window.cancelAnimationFrame =\n" +
				"            window[c[a] + \"CancelAnimationFrame\"] ||\n" +
				"            window[c[a] + \"CancelRequestAnimationFrame\"];\n" +
				"        }\n" +
				"        if (!window.requestAnimationFrame) {\n" +
				"          window.requestAnimationFrame = function (h, e) {\n" +
				"            var d = new Date().getTime();\n" +
				"            var f = Math.max(0, 16 - (d - b));\n" +
				"            var g = window.setTimeout(function () {\n" +
				"              h(d + f);\n" +
				"            }, f);\n" +
				"            b = d + f;\n" +
				"            return g;\n" +
				"          };\n" +
				"        }\n" +
				"        if (!window.cancelAnimationFrame) {\n" +
				"          window.cancelAnimationFrame = function (d) {\n" +
				"            clearTimeout(d);\n" +
				"          };\n" +
				"        }\n" +
				"      })();\n" +
				"      var Point = (function () {\n" +
				"        function Point(x, y) {\n" +
				"          this.x = typeof x !== \"undefined\" ? x : 0;\n" +
				"          this.y = typeof y !== \"undefined\" ? y : 0;\n" +
				"        }\n" +
				"        Point.prototype.clone = function () {\n" +
				"          return new Point(this.x, this.y);\n" +
				"        };\n" +
				"        Point.prototype.length = function (length) {\n" +
				"          if (typeof length == \"undefined\")\n" +
				"            return Math.sqrt(this.x * this.x + this.y * this.y);\n" +
				"          this.normalize();\n" +
				"          this.x *= length;\n" +
				"          this.y *= length;\n" +
				"          return this;\n" +
				"        };\n" +
				"        Point.prototype.normalize = function () {\n" +
				"          var length = this.length();\n" +
				"          this.x /= length;\n" +
				"          this.y /= length;\n" +
				"          return this;\n" +
				"        };\n" +
				"        return Point;\n" +
				"      })();\n" +
				"      var Particle = (function () {\n" +
				"        function Particle() {\n" +
				"          this.position = new Point();\n" +
				"          this.velocity = new Point();\n" +
				"          this.acceleration = new Point();\n" +
				"          this.age = 0;\n" +
				"        }\n" +
				"        Particle.prototype.initialize = function (x, y, dx, dy) {\n" +
				"          this.position.x = x;\n" +
				"          this.position.y = y;\n" +
				"          this.velocity.x = dx;\n" +
				"          this.velocity.y = dy;\n" +
				"          this.acceleration.x = dx * settings.particles.effect;\n" +
				"          this.acceleration.y = dy * settings.particles.effect;\n" +
				"          this.age = 0;\n" +
				"        };\n" +
				"        Particle.prototype.update = function (deltaTime) {\n" +
				"          this.position.x += this.velocity.x * deltaTime;\n" +
				"          this.position.y += this.velocity.y * deltaTime;\n" +
				"          this.velocity.x += this.acceleration.x * deltaTime;\n" +
				"          this.velocity.y += this.acceleration.y * deltaTime;\n" +
				"          this.age += deltaTime;\n" +
				"        };\n" +
				"        Particle.prototype.draw = function (context, image) {\n" +
				"          function ease(t) {\n" +
				"            return --t * t * t + 1;\n" +
				"          }\n" +
				"          var size = image.width * ease(this.age / settings.particles.duration);\n" +
				"          context.globalAlpha = 1 - this.age / settings.particles.duration;\n" +
				"          context.drawImage(\n" +
				"            image,\n" +
				"            this.position.x - size / 2,\n" +
				"            this.position.y - size / 2,\n" +
				"            size,\n" +
				"            size\n" +
				"          );\n" +
				"        };\n" +
				"        return Particle;\n" +
				"      })();\n" +
				"      var ParticlePool = (function () {\n" +
				"        var particles,\n" +
				"          firstActive = 0,\n" +
				"          firstFree = 0,\n" +
				"          duration = settings.particles.duration;\n" +
				" \n" +
				"        function ParticlePool(length) {\n" +
				"          particles = new Array(length);\n" +
				"          for (var i = 0; i < particles.length; i++)\n" +
				"            particles[i] = new Particle();\n" +
				"        }\n" +
				"        ParticlePool.prototype.add = function (x, y, dx, dy) {\n" +
				"          particles[firstFree].initialize(x, y, dx, dy);\n" +
				"          firstFree++;\n" +
				"          if (firstFree == particles.length) firstFree = 0;\n" +
				"          if (firstActive == firstFree) firstActive++;\n" +
				"          if (firstActive == particles.length) firstActive = 0;\n" +
				"        };\n" +
				"        ParticlePool.prototype.update = function (deltaTime) {\n" +
				"          var i;\n" +
				"          if (firstActive < firstFree) {\n" +
				"            for (i = firstActive; i < firstFree; i++)\n" +
				"              particles[i].update(deltaTime);\n" +
				"          }\n" +
				"          if (firstFree < firstActive) {\n" +
				"            for (i = firstActive; i < particles.length; i++)\n" +
				"              particles[i].update(deltaTime);\n" +
				"            for (i = 0; i < firstFree; i++) particles[i].update(deltaTime);\n" +
				"          }\n" +
				"          while (\n" +
				"            particles[firstActive].age >= duration &&\n" +
				"            firstActive != firstFree\n" +
				"          ) {\n" +
				"            firstActive++;\n" +
				"            if (firstActive == particles.length) firstActive = 0;\n" +
				"          }\n" +
				"        };\n" +
				"        ParticlePool.prototype.draw = function (context, image) {\n" +
				"          if (firstActive < firstFree) {\n" +
				"            for (i = firstActive; i < firstFree; i++)\n" +
				"              particles[i].draw(context, image);\n" +
				"          }\n" +
				"          if (firstFree < firstActive) {\n" +
				"            for (i = firstActive; i < particles.length; i++)\n" +
				"              particles[i].draw(context, image);\n" +
				"            for (i = 0; i < firstFree; i++) particles[i].draw(context, image);\n" +
				"          }\n" +
				"        };\n" +
				"        return ParticlePool;\n" +
				"      })();\n" +
				"      (function (canvas) {\n" +
				"        var context = canvas.getContext(\"2d\"),\n" +
				"          particles = new ParticlePool(settings.particles.length),\n" +
				"          particleRate =\n" +
				"            settings.particles.length / settings.particles.duration, \n" +
				"          time;\n" +
				"        function pointOnHeart(t) {\n" +
				"          return new Point(\n" +
				"            160 * Math.pow(Math.sin(t), 3),\n" +
				"            130 * Math.cos(t) -\n" +
				"              50 * Math.cos(2 * t) -\n" +
				"              20 * Math.cos(3 * t) -\n" +
				"              10 * Math.cos(4 * t) +\n" +
				"              25\n" +
				"          );\n" +
				"        }\n" +
				"        var image = (function () {\n" +
				"          var canvas = document.createElement(\"canvas\"),\n" +
				"            context = canvas.getContext(\"2d\");\n" +
				"          canvas.width = settings.particles.size;\n" +
				"          canvas.height = settings.particles.size;\n" +
				"          function to(t) {\n" +
				"            var point = pointOnHeart(t);\n" +
				"            point.x =\n" +
				"              settings.particles.size / 2 +\n" +
				"              (point.x * settings.particles.size) / 350;\n" +
				"            point.y =\n" +
				"              settings.particles.size / 2 -\n" +
				"              (point.y * settings.particles.size) / 350;\n" +
				"            return point;\n" +
				"          }\n" +
				"          context.beginPath();\n" +
				"          var t = -Math.PI;\n" +
				"          var point = to(t);\n" +
				"          context.moveTo(point.x, point.y);\n" +
				"          while (t < Math.PI) {\n" +
				"            t += 0.01;\n" +
				"            point = to(t);\n" +
				"            context.lineTo(point.x, point.y);\n" +
				"          }\n" +
				"          context.closePath();\n" +
				"          context.fillStyle = \"#ea80b0\";\n" +
				"          context.fill();\n" +
				"          var image = new Image();\n" +
				"          image.src = canvas.toDataURL();\n" +
				"          return image;\n" +
				"        })();\n" +
				"        function render() {\n" +
				"          requestAnimationFrame(render);\n" +
				"          var newTime = new Date().getTime() / 1000,\n" +
				"            deltaTime = newTime - (time || newTime);\n" +
				"          time = newTime;\n" +
				"          context.clearRect(0, 0, canvas.width, canvas.height);\n" +
				"          var amount = particleRate * deltaTime;\n" +
				"          for (var i = 0; i < amount; i++) {\n" +
				"            var pos = pointOnHeart(Math.PI - 2 * Math.PI * Math.random());\n" +
				"            var dir = pos.clone().length(settings.particles.velocity);\n" +
				"            particles.add(\n" +
				"              canvas.width / 2 + pos.x,\n" +
				"              canvas.height / 2 - pos.y,\n" +
				"              dir.x,\n" +
				"              -dir.y\n" +
				"            );\n" +
				"          }\n" +
				"          particles.update(deltaTime);\n" +
				"          particles.draw(context, image);\n" +
				"        }\n" +
				"        function onResize() {\n" +
				"          canvas.width = canvas.clientWidth;\n" +
				"          canvas.height = canvas.clientHeight;\n" +
				"        }\n" +
				"        window.onresize = onResize;\n" +
				"        setTimeout(function () {\n" +
				"          onResize();\n" +
				"          render();\n" +
				"        }, 10);\n" +
				"      })(document.getElementById(\"pinkboard\"));\n" +
				" \n" +
				"    </script>\n" +
				"  </body>\n" +
				"</html>";
	}

}
