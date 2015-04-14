var ProgressBar = React.createClass({
    render: function () {
        return (
            <progress value={this.props.status} max="100"></progress>
        );
    }
});

var AddSize = React.createClass({
    render: function () {
        return (
            <button disabled={this.props.exceeded} onClick={this.props.callBack}>Add 5Mb</button>
        );
    }
});

var IndexedDB = React.createClass({
    getInitialState: function () {
        return {allotedSize: this.props.allotedSize, exceeded: false};
    },
    addData: function () {
        var newSize = (parseInt(this.state.allotedSize) + 5);
        this.setState({allotedSize: newSize, exceeded: newSize === 100});
    },
    render: function () {
        return (
            <div className="indexedDBWrapper">
                <ProgressBar status={this.state.allotedSize}/>
                <AddSize callBack={this.addData} exceeded={this.state.exceeded}/>
            </div>
        );
    }
});


var Quota = React.createClass({
    getInitialState: function () {
        return {allotedSize: this.props.allotedSize, exceeded: false};
    },
    addSize: function () {
        var newSize = (parseInt(this.state.allotedSize) + 5);
        this.setState({allotedSize: newSize, exceeded: newSize === 100});
    },
    render: function () {
        return (
            <div className="quota">
                <ProgressBar status={this.state.allotedSize}/>
                <AddSize callBack={this.addSize} exceeded={this.state.exceeded}/>
            </div>
        );
    }
});


var Wrapper = React.createClass({
    render: function () {
        return (
            <div className="wrapper">
                <h1>Quota Allocation</h1>
                <Quota allotedSize="0"/>

                <h1>Indexed Db Size</h1>
                <IndexedDB allotedSize="0"/>
            </div>
        );
    }
});

React.render(
    <Wrapper/>,
    document.getElementById('content')
);
