import React from 'react';


const FlowRow = ({row}) => {
    return (
      <tr>
        <td>{row.startTime}</td>
        <td>{row.lastTime}</td>
        <td>{row.clientIp}</td>
        <td>{row.clientBytes}</td>
        <td>{row.clientHgs}</td>
        <td>{row.userName}</td>
        <td>{row.port}</td>
        <td>{row.protocol}</td>
      </tr>
    );
};

export default FlowRow;
