import React, { Component } from 'react';
import classNames from 'classnames';

import '../../styles/components/menu.scss';
import Groups from '../group/Groups';
import Welcome from '../welcome/Welcome';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";


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
      <Router>
        <div className="header">
          <nav className={classNames('menu', {isOpen: this.state.showHideClassName})} id="main-menu">
            <button className="menu-toggle" id="toggle-menu" onClick={this.toggleMenu}>
              toggle menu
            </button>
            <div className='menu-dropdown'>
              <ul className="site-nav">
                <li><Link to="/">Home</Link></li>
                <li><Link to="/bands">Bands</Link></li>
              </ul>
            </div>
          </nav>
          <Route exact path="/" component={Welcome} />
          <Route path="/bands" component={Groups} />
        </div>
      </Router>
    );
  }
}

export default Menu;
