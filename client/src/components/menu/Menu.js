import React, { Component } from 'react';
import classNames from 'classnames';

import '../../styles/components/menu.scss';
import Groups from '../group/Groups';
import Welcome from '../welcome/Welcome';
import Flows from '../flows/Flows';
import Restaurants from '../restaurants/Restaurants';
import About from '../about/About';
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
        <div>
          <div className="header">
            <nav className={classNames('menu', {isOpen: this.state.showHideClassName})} id="main-menu">
              <button className="menu-toggle" id="toggle-menu" onClick={this.toggleMenu}>
                toggle menu
              </button>
              <div className='menu-dropdown'>
                <ul className="site-nav">
                  <li><Link to="/">Home</Link></li>
                  <li><Link to="/bands">Bands</Link></li>
                  <li><Link to="/flows">Flows</Link></li>
                  <li><Link to="/restaurants">Restaurants</Link></li>
                  <li><Link to="/about">About</Link></li>
                </ul>
              </div>
            </nav>
          </div>
          <Route exact path="/" component={Welcome} />
          <Route path="/bands" component={Groups} />
          <Route path="/flows" component={Flows} />
          <Route path="/restaurants" component={Restaurants} />
          <Route path="/about" component={About} />
        </div>

      </Router>
    );
  }
}

export default Menu;
