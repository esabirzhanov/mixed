import React, { Component } from 'react';
import '../../styles/components/flows.scss';
import FlowRow from './FlowRow';


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
        this.setState({
            flows: this.state.flows.concat(flowsApi)
        });
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

export default Flows