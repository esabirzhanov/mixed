import React, { Component } from 'react';
import '../../styles/components/flows.scss';
import FlowRow from './FlowRow';
import moment from 'moment';


const flowsApi = [
    {id: 1165, startTime: "2018-10-09T16:59:52Z", lastTime: "2018-10-09T17:09:52Z",  port: 443, protocol: 6 },
    {id: 1176, startTime: "2018-10-09T16:59:52Z", lastTime: "2018-10-09T17:09:52Z",  port: 53, protocol: 17}
]

class Flows extends Component {
    constructor(props) {
        super(props);
        this.state = {
            error: null,
            flows: []
        };
        this.onChange = this.onChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit = ev => {
        ev.preventDefault();
        console.log('handleSubmit');

    }

    onChange = ev => {
        console.log('onChange')
    }

    componentDidMount() {

        const _2pow32 = Math.pow(2, 32);

        const WS_URL = 'ws://localhost:3000/streamed/flows';
        const ws = new WebSocket(WS_URL);
        ws.binaryType = "arraybuffer"; 1
        ws.onopen = () => console.log(`Connected to ${WS_URL}`);

        let flowBuffer = [];

        ws.onmessage = message => {
            const data = message.data;
            const typeArray32 = new Uint32Array(data);
           

            const id = consumeLong(typeArray32, 0, _2pow32); 
      


            /*
            const idL = typeArray32[1];
            const idS = typeArray32[0]; 
            const idLShifted = idL * _2pow32;
            const idRes = idLShifted + idS;
            */
         
            const saTsL = typeArray32[3];
            const saTsS = typeArray32[2]; 
            const saTsShifted = saTsL * _2pow32;
            const saTsRes = saTsShifted + saTsS;
            const saTs = new Date(saTsRes);
          
            const laTsL = typeArray32[5];
            const laTsS = typeArray32[4]; 
            const laTsShifted = laTsL * _2pow32;
            const laTsRes = laTsShifted + laTsS;
            const laTs = new Date(laTsRes);

            const clientIp = typeArray32[6]

           

            const sp = typeArray32[7]; 
            const protocol = typeArray32[8]; 

            const hgsLength = typeArray32[9]; 
            

            const usrNameLength = new Uint8Array(data, 40, 1)[0];
            let usrName
            if (usrNameLength === 0) 
                usrName = ''
            else 
                usrName = new TextDecoder("utf-8").decode(new Uint8Array(data, 41, usrNameLength));
            
            

    

            console.log("asjdhgajdg " + hgsLength)
    

            const flow = {
                id: id,
                startTime:  moment(saTs).format(), 
                lastTime:   moment(laTs).format(),
                port:   sp,
                protocol:   protocol,
                userName: usrName
            }
            flowBuffer = flowBuffer.concat([flow])

            if (flowBuffer.length === 86) {
                ws.close();
            }
        };
        
        ws.onclose = message => {
            console.log("Done.. we are closed..  now updating React state!!!")
            this.setState({
                flows: this.state.flows.concat(flowBuffer)
             });
        };
    }

    render() {
        const rows = [];
        this.state.flows.forEach((flow) => {
            rows.push( <FlowRow row={flow} key={flow.id}/> );
        });
    
        return (
            <div className="flows">
                <table>
                    <thead>
                        <tr>
                            <th>Start Time</th>
                            <th>Last Time</th>
                            <th>User Name</th>
                            <th>Service Port</th>
                            <th>Protocol</th>
                        </tr>
                    </thead>
                    <tbody>{rows}</tbody>
                </table>
                <form onSubmit={this.handleSubmit}>
                    <label>
                         Port:
                        <input type="text" value={this.state.value} onChange={this.handleChange} />
                    </label>
                    <input type="submit" value="Submit" />
                </form>
            </div>
        );
    }
}

const consumeLong = (typeArray, index, _2pow32) => {
    const l = typeArray[index + 1];
    const s = typeArray[index]; 
    const lShifted = l * _2pow32;
    return lShifted + s;
}

export default Flows