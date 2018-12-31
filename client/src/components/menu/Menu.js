import React, { Component } from 'react';
import classNames from 'classnames';

import '../../styles/components/menu.scss';


class Menu extends Component {

  constructor(props) {
      super(props);
      this.state = {
        showHideClassName: true
      };
      this.toggleMenu = this.toggleMenu.bind(this);
  }

  toggleMenu = () => {
    if (this.state.showHideClassName) {
      this.setState ({showHideClassName: false});
    } else {
      this.setState ({showHideClassName: true});
    }
  }

  componentDidCatch(err, info) {
        console.error(err);
        console.error(info);
        this.setState(() => ({
            error: err,
        }));
  }

  render() {

    return (
      <div className="header">
        <nav className={classNames('menu', {isOpen: this.state.showHideClassName})} id="main-menu">
          <button className="menu-toggle" id="toggle-menu" onClick={this.toggleMenu}>
            toggle menu
          </button>
          <div className='menu-dropdown'>
            <ul className="site-nav">
              <li><a href="/">Home</a></li>
              <li><a href="/bands">Bands</a></li>
              <li><a href="/flows">Flows</a></li>
              <li><a href="/top_reports">Top Reports</a></li>
              <li><a href="/about">About</a></li>
              </ul>
          </div>
        </nav>
      </div>
    );
  }
}

export default Menu;
