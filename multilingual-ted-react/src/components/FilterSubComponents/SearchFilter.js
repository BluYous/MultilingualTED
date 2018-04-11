import React, {Component} from 'react';
import {Input} from 'antd';
import {PubSub} from 'pubsub-js';

class SearchFilter extends Component {
    handleSearch = (value) => {
        PubSub.publish('searchFilter', value);
    };
    
    render() {
        return (
            <Input.Search
                placeholder="Input search text"
                onSearch={this.handleSearch}
                enterButton
            />
        )
    }
}

export default SearchFilter;