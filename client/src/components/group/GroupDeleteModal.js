import React, { Component } from 'react';
import '../../styles/components/modal.scss';

import api from "../../shared/http";


class GroupDeleteModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      groupName: null
    };
    this.hideModal = this.hideModal.bind(this);
    this.deleteGroup = this.deleteGroup.bind(this);
  }

  hideModal = e => {
    e.preventDefault();
    this.props.handleClose();
    this.state = {
      groupName: null
    };
  }

  deleteGroup = e => {
    this.props.handleDelete(this.props.groupId);
    this.hideModal(e);
  }

  componentDidUpdate(prevProps, prevState) {
    if (this.props.groupId !== -1 && this.props.show === true && prevProps.show === false) {
      api.getGroup(this.props.groupId, data => {
        this.setState({
          groupName: data.name,
        });
      });
    }
  }

  render() {
    const showHideClassName = this.props.show ? "modal display-block" : "modal display-none";
    const deleteWarning = `Are you sure to remove "${this.state.groupName}"?`

    return (
      <div className={showHideClassName}>
        <div className="modal-backdrop"></div>
        <div className="modal-body">
          <button className="modal-button-cross" onClick={this.hideModal}></button>
          <div className="modal-content">
            {deleteWarning}
          </div>

          <div className="control-buttons-container">
            <div className="control-buttons">
              <button className="control-button" onClick={this.hideModal}>Close</button>
              <button className="control-button" onClick={this.deleteGroup}>Delete</button>
            </div>
          </div>
        </div>
      </div>
    );
  }

}

export default GroupDeleteModal;
