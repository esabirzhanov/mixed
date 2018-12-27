import React, { Component } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';

import api from "../../shared/http";
import Welcome from '../welcome/Welcome';
import Group from '../group/Group';
import Modal from "../modal/Modal";

import '../../styles/components/menu.scss';


class App extends Component {
  constructor(props) {
      super(props);
      this.state = {
          error: null,
          loading: false,
          groups: [],
          deleteConfirmDialog: false,
          groupDetailsDialog: false,
          groupId: -1,
          groupName: null,
          showHideClassName: true
      };
      this.getGroups = this.getGroups.bind(this);
      this.getGroup = this.getGroup.bind(this);
      this.deleteGroup = this.deleteGroup.bind(this);
      this.hideDeleteGroupModal = this.hideDeleteGroupModal.bind(this);
      this.openDeleteGroupModal = this.openDeleteGroupModal.bind(this);
      this.toggleMenu = this.toggleMenu.bind(this);
  }

  static propTypes = {
    children: PropTypes.node,
  };

  componentDidMount() {
    this.getGroups();
  }

  componentDidCatch(err, info) {
        console.error(err);
        console.error(info);
        this.setState(() => ({
            error: err,
        }));
  }

  openDeleteGroupModal = (groupId, groupName) => {
    this.setState({
      groupId: groupId,
      groupName: groupName,
      deleteConfirmDialog: true
    });
  }

  hideDeleteGroupModal = () => {
    this.setState({
      groupId: -1,
      groupName: null,
      deleteConfirmDialog: false
    });
  }

  deleteGroup = groupId => {
    api.remove(this.state.groupId, () => {
      api.getGroups(data => {
        this.setState({
          groups: data
        });
      })
    })
    .catch(err => {
      this.setState(() => ({ error: err }));
    });

    this.hideDeleteGroupModal();
  }

  toggleMenu = () => {
    if (this.state.showHideClassName) {
      this.setState ({showHideClassName: false});
    } else {
      this.setState ({showHideClassName: true});
    }
  }


  getGroups =  () => {
    api.getGroups(data => {
      this.setState({
        groups: this.state.groups.concat(data)
      });
    })
    .catch(err => {
      this.setState(() => ({ error: err }));
    });
  }

  getGroup = groupId => {
    api.getGroup(groupId, data => {
      this.setState({
        group: data
      });
    })
    .catch(err => {
      this.setState(() => ({ error: err }));
    });
  }

  render() {
    const titleMessage = this.state.groups.length === 0 ? 'Sorry, no Legendary Band..' : 'Legendary Bands..';
    const deleteWarning = `Are you sure to remove "${this.state.groupName}"?`

    return (
      <div className="app">
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
                <li className="nav-right">
                  <a href="/about">About</a>
                  </li>
              </ul>
          </div>
        </nav>
          <Welcome key="welcome" />
        </div>



        <div className="groups clearfix">
          <h2>{titleMessage}</h2>

          <div>
            {this.state.groups.map(({ id, name, category, picture, description }) => {
              return <Group id={id} key={id} name={name}
                category={category} picture={picture} description={description}
                  handleDelete={this.openDeleteGroupModal} />;
            })}
          </div>
        </div>

        <div id="deleteConfirm">
          <Modal show={this.state.deleteConfirmDialog}>
            <button className="modal-button-cross" onClick={this.hideDeleteGroupModal}></button>
            <div className="modal-content">
              {deleteWarning}
            </div>

            <div className="control-buttons-container">
              <div className="control-buttons">
                <button className="control-button" onClick={this.hideDeleteGroupModal}>Close</button>
                <button className="control-button" onClick={this.deleteGroup}>Delete</button>
              </div>
            </div>
          </Modal>
        </div>

        <div id="groupDetails">
          <Modal show={this.state.groupDetailsDialog}>
            <button className="modal-button-cross" onClick={this.hideModal}></button>
            <div className="modal-content">
              Sorry, under construction ..
            </div>
          </Modal>
        </div>

      </div>
    );
  }
}

export default App;
