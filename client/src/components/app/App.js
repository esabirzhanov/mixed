import React, { Component } from 'react';
import PropTypes from 'prop-types';

import Menu from '../menu/Menu';
import Groups from '../group/Groups';

import '../../styles/components/app.scss';
import '../../styles/components/menu.scss';

/**
 * The app component serves as a root for the project and renders either children,
 * the error state, or a loading state
 */
class App extends Component {

  static propTypes = {
    children: PropTypes.node,
  };

  componentDidCatch(err, info) {
        console.error(err);
        console.error(info);
        this.setState(() => ({
            error: err,
        }));
  }

  render() {
    return (
      <div className="app">
        <Menu></Menu>
        <Groups></Groups>

      </div>
    );
  }
}

export default App;
