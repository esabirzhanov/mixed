import React, { Component } from 'react';
import '../../styles/components/controlButtons.scss';
import '../../styles/global.scss';
import '../../styles/components/group.scss';

class Group extends Component {

  constructor(props) {
    super(props);
    this.remove = this.remove.bind(this);
    this.openDetails = this.openDetails.bind(this);
    this.openPosts = this.openPosts.bind(this);
  }

  remove(e) {
    e.preventDefault();
    const id = this.props.id;
    const name = this.props.name
    this.props.handleDelete(id, name)
  }

  openDetails(e) {
    const id = this.props.id;
    e.preventDefault();
    this.props.handleDetails(id)
  }

  openPosts(e) {
    const id = this.props.id;
    e.preventDefault();
    this.props.handlePosts(id)
  }

  render() {
    const picturePath = require(`../../static/images/${this.props.picture}`);
    return (
      <div key={this.props.id} className='group'>

        <div className="media">
          <img className='media-image' src={picturePath}  alt='no'/>
          <div className='media-body'>
            <div><b>Name:</b> {this.props.name}</div>
            <div><b>Category:</b> {this.props.category}</div>
            <div>{this.props.description}</div>
          </div>
        </div>

        <div className="group-buttons">
          <div>
              <button className="posts-button" onClick={this.openPosts}>Posts</button>
          </div>

          <div className="control-buttons">
            <button className="control-button" onClick={this.openDetails} >Details</button>
            <button className="control-button" onClick={this.remove}>Delete</button>
          </div>
        </div>

      </div>
    );
 }
}
export default Group;
