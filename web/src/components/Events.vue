<template>
	<div>
		<v-alert v-for="ev in events" :key="ev.id" :type="ev.category" dense dismissible @input="removeEvent(ev)">
			{{ ev.text }}
		</v-alert>
		<v-divider class="mb-3" v-if="events && events.length > 0" />
	</div>
</template>

<script>
export default {
	name: "Events",
	data() {
		return {
			id: 0,
			events: [],
		};
	},
	mounted() {
		let ev = this.addEvent;
		this.$em.on("error", function(error) {
			ev({
				category: "error",
				text: error.message,
			});
		});
		this.$em.on("warning", function(warning) {
			ev({
				category: "warning",
				text: warning,
			});
		});
	},
	methods: {
		removeEvent(ev) {
			this.events.splice(this.events.indexOf(ev), 1);
		},
		addEvent(ev) {
			ev = { ...ev, id: this.id };
			this.events.push(ev);
			this.id++;
		},
	},
};
</script>
