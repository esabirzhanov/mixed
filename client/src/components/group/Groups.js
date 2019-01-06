import React, { Component } from 'react';
import PropTypes from 'prop-types';
import api from "../../shared/http";
import Group from './Group';
import Modal from "../modal/Modal";
import GroupDetailsModal from "./GroupDetailsModal";
import GroupPostsModal from "./GroupPostsModal";

class Groups extends Component {

  constructor(props) {
      super(props);
      this.state = {
          error: null,
          loading: false,
          groups: [],
          group: null,
          deleteConfirmDialog: false,
          groupDetailsDialog: false,
          groupPostsDialog: false,
          groupId: -1,
          groupName: null
        };
      this.getGroups = this.getGroups.bind(this);
      this.deleteGroup = this.deleteGroup.bind(this);
      this.hideDeleteGroupModal = this.hideDeleteGroupModal.bind(this);
      this.openDeleteGroupModal = this.openDeleteGroupModal.bind(this);
      this.hideGroupDetailsModal = this.hideGroupDetailsModal.bind(this);
      this.openGroupDetailsModal = this.openGroupDetailsModal.bind(this);
      this.hideGroupPostsModal = this.hideGroupPostsModal.bind(this);
      this.openGroupPostsModal = this.openGroupPostsModal.bind(this);
      this.updateGroup = this.updateGroup.bind(this);
  }

  componentDidMount() {
    this.getGroups();
  }

  componentDidCatch(err, info) {
    console.error(err);
    console.error(info);
    this.setState(() => ({
      error: err
    }));
  }

  openGroupPostsModal = (groupId) => {
    this.setState({
        groupId: groupId,
        groupPostsDialog: true
      });
  }

  hideGroupPostsModal = () => {
      this.setState({
        groupId: -1,
        groupPostsDialog: false
      });
  }

  openGroupDetailsModal = (groupId) => {
    api.getGroup(groupId, data => {
      this.setState({
        groupId: groupId,
        group: data,
        groupDetailsDialog: true
      });
    });
  }

  hideGroupDetailsModal = () => {
      this.setState({
        groupId: -1,
        group: null,
        groupDetailsDialog: false
      });
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

updateGroup =  e => {
  e.preventDefault();
}

render() {
    const titleMessage = this.state.groups.length === 0 ? 'Sorry, no Legendary Band..' : 'Legendary Bands..';
    const deleteWarning = `Are you sure to remove "${this.state.groupName}"?`

    return (
      <div>
        <div className="groups clearfix">
          <div className="title"></div>
          <h2>{titleMessage}</h2>
          <div>
            {this.state.groups.map(({ id, name, category, picture, description }) => {
              return <Group id={id} key={id} name={name}
                category={category} picture={picture} description={description}
                handleDelete={this.openDeleteGroupModal}
                  handleDetails={this.openGroupDetailsModal}
                  handlePosts={this.openGroupPostsModal} />;
              })}
          </div>
        </div>

        <div id="deleteConfirm" className="centered">
          <Modal show={this.state.deleteConfirmDialog} >
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
        <GroupDetailsModal show={this.state.groupDetailsDialog}>
          <button className="modal-button-cross" onClick={this.hideGroupDetailsModal}></button>
          <div className="modal-content">
              <form className="group-details-form">
                <h3>Group Info</h3>
                <p>
                  <label htmlFor="groupName">Group Name</label>
                  <input id="groupName" type="text" name="groupName"
                    defaultValue={this.state.group === null ? "" : this.state.group.name}/>
                </p>
                <p>
                  <label htmlFor="category">Category</label>
                  <input id="category" type="text" name="category"
                        defaultValue={this.state.group === null ? "" : this.state.group.category}/>
                </p>
                <p>
                  <label htmlFor="description">Description</label>
                  <textarea id="description" rows="10"
                    value={this.state.group === null ? "" : this.state.group.description} readOnly={false} onChange={this.updateGroup}/>
                </p>
                <button>Cancel</button>
                <button>Update</button>
             </form>
          </div>
        </GroupDetailsModal>
      </div>

      <div id="groupPosts">
        <GroupPostsModal show={this.state.groupPostsDialog}>
          <button className="modal-button-cross" onClick={this.hideGroupPostsModal}></button>
          <div className="modal-content">
            <h1>Sorry, we under construction yet ..</h1>
          </div>
        </GroupPostsModal>
      </div>

    </div>
  );
  }
}

export default Groups;
