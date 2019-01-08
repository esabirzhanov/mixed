import React, { Component } from 'react';
import '../../styles/components/groupDetailsModal.scss';
import api from "../../shared/http";


class GroupDetailsModal extends Component {

    constructor(props) {
        super(props);
        this.state = {
            group: null,
        };
    }

    componentDidUpdate(prevProps, prevState) {
        if (this.props.groupId !== -1 && this.props.show !== prevProps.show) {
           api.getGroup(this.props.groupId, data => {
                this.setState({
                    group: data,
                });
            });
        }
    }



    render() {
        const showHideClassName = this.props.show ? "modal display-block" : "modal display-none";
        return (
            <div className={showHideClassName}>
                <div className="modal-backdrop"></div>
                <div className="modal-body">
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

                </div>
            </div>

        );
    }
}

export default GroupDetailsModal;
