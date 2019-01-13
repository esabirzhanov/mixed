import React, { Component } from 'react';
import PropTypes from 'prop-types';
import api from "../../shared/http";
import Group from './Group';
import GroupDetailsModal from "./GroupDetailsModal";
import GroupDeleteModal from "./GroupDeleteModal";
import GroupPostsModal from "./GroupPostsModal";
import Loader from "../Loader";

class Groups extends Component {

  constructor(props) {
      super(props);
      this.state = {
          error: null,
          loading: true,
          groups: [],
          deleteConfirmDialog: false,
          groupDetailsDialog: false,
          groupPostsDialog: false,
          groupId: -1
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
    this.setState({
      groupId: groupId,
      groupDetailsDialog: true
    });
  }

  hideGroupDetailsModal = () => {
    this.setState({
      groupId: -1,
      groupDetailsDialog: false
    });
  }

  openDeleteGroupModal = (groupId, groupName) => {
    this.setState({
      groupId: groupId,
      deleteConfirmDialog: true
    });
  }

  hideDeleteGroupModal = () => {
    this.setState({
      groupId: -1,
      deleteConfirmDialog: false
    });
  }

  deleteGroup = groupId => {
    api.remove(this.state.groupId, () => {
      this.getGroups()
    })
    .catch(err => {
      this.setState(() => ({ error: err }));
    });
    this.hideDeleteGroupModal();
  }

  getGroups = () => {
    api.getGroups(data => {
      this.setState({
        loading: false,
        groups: this.state.groups.concat(data)
      });
    })
    .catch(err => {
      this.setState(() => ({ error: err }));
    });
  }

  updateGroup = e => {
    e.preventDefault();
  }

render() {
    const titleMessage = this.state.groups.length === 0 ? 'Sorry, no Legendary Band..' : 'Legendary Bands..';
    const deleteWarning = `Are you sure to remove "${this.state.groupName}"?`

    return (
      <div>
        {this.state.loading ? (
          <div className="loading">
            <Loader/>
          </div>
        ) : (
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
              <GroupDeleteModal show={this.state.deleteConfirmDialog} groupId={this.state.groupId}
                handleClose={this.hideDeleteGroupModal} handleDelete={this.deleteGroup}  />
            </div>

            <div id="groupDetails">
              <GroupDetailsModal show={this.state.groupDetailsDialog}
                groupId={this.state.groupId} handleClose={this.hideGroupDetailsModal} />
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
        )}
      </div>
  );
  }
}

export default Groups;
