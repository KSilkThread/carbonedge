import { AfterViewInit, Component, ViewChild } from '@angular/core';
import * as mqtt from 'mqtt';
import * as am5 from "@amcharts/amcharts5";
import * as am5map from "@amcharts/amcharts5/map";
import am5geodata_worldLow from "@amcharts/amcharts5-geodata/worldLow";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterViewInit {
  title = 'ng-visu';

  ngAfterViewInit() {

    // create a client
    let client = mqtt.connect('ws://127.0.0.1', {
      protocolId: 'MQIsdp',
      protocolVersion: 3,
      port: 1885,
      clean: true,
    })

    client.subscribe('/path');
    client.subscribe('/clear');

    client.on('message', (topic, message) => {
      // message is Buffer
      console.log(topic.toString());
      if (topic.toString() == "/clear") {
        location.reload();
      }
      if (topic.toString() == "/path") {


        var pathList = JSON.parse(message.toString());


      }
    })

  }

}
