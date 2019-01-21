import React from 'react';


const FlowRow = ({row}) => {

  console.log(row.id)


    return (
      <tr>
        <td>{row.startTime}</td>
        <td>{row.lastTime}</td>
        <td>{row.port}</td>
        <td>{row.protocol}</td>
      </tr>
    );
};

export default FlowRow;
