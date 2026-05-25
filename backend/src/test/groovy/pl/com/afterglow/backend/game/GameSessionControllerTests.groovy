package pl.com.afterglow.backend.game

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.springframework.boot.test.web.server.LocalServerPort
import pl.com.afterglow.backend.IntegrationTestSpecification

class GameSessionControllerTests extends IntegrationTestSpecification {

	@LocalServerPort
	int port

	def 'creates setup session with adult-confirmed host'() {
		when:
		def response = post('/api/game-sessions', [
				hostNickname : 'Host',
				confirmedAdult: true,
				settings      : [
						startSpiceLevel: 'warmup',
						maxSpiceLevel  : 'courage',
						pace           : 'standard'
				]
		])

		then:
		response.status == 201

		and:
		def session = response.body
		session.code ==~ /AGL-[0-9]{3}/
		session.status == 'setup'
		session.players[0].nickname == 'Host'
		session.players[0].host == true
		session.players[0].comfortProfile.confirmedAdult == true
		session.settings.minPlayers == 2
		session.settings.maxPlayers == 8
	}

	def 'rejects player without adult confirmation'() {
		when:
		def response = post('/api/game-sessions', [
				hostNickname : 'Host',
				confirmedAdult: false
		])

		then:
		response.status == 409
		response.body.error == 'invalid_game_action'
	}

	def 'starts session and deals four cards to each player'() {
		given:
		def session = createSession('Host')
		addPlayer(session.id, 'Guest')

		when:
		def response = post("/api/game-sessions/${session.id}/start", null)

		then:
		response.status == 200

		and:
		def started = response.body
		started.status == 'in_progress'
		started.currentRound == 1
		started.currentTurnPlayerId == started.players[0].id
		started.players.size() == 2
		started.players.every { it.hand.size() == 4 }
		started.players.every { player ->
			player.hand.every { cardInstance ->
				cardInstance.card.type
				cardInstance.card.spiceLevel
				cardInstance.card.target
				cardInstance.card.boundaries != null
			}
		}
	}

	def 'does not start with fewer than two players'() {
		given:
		def session = createSession('Solo')

		when:
		def response = post("/api/game-sessions/${session.id}/start", null)

		then:
		response.status == 409
		response.body.error == 'invalid_game_action'
	}

	def 'plays and completes current turn card'() {
		given:
		def started = startedTwoPlayerSession()
		def currentPlayer = started.players.find { it.id == started.currentTurnPlayerId }
		def otherPlayer = started.players.find { it.id != currentPlayer.id }
		def cardInstance = playableCard(currentPlayer)

		when:
		def playResponse = post("/api/game-sessions/${started.id}/cards/play",
				playCardBody(currentPlayer, cardInstance, otherPlayer))

		then:
		playResponse.status == 200

		and:
		def afterPlay = playResponse.body
		afterPlay.currentCard.cardInstanceId == cardInstance.instanceId
		afterPlay.currentCard.playerId == currentPlayer.id
		afterPlay.players.find { it.id == currentPlayer.id }.hand.size() == 4

		when:
		def completeResponse = post("/api/game-sessions/${started.id}/current-card/complete", [
				playerId: currentPlayer.id
		])

		then:
		completeResponse.status == 200

		and:
		def afterComplete = completeResponse.body
		afterComplete.currentCard == null
		afterComplete.currentTurnPlayerId == otherPlayer.id
		afterComplete.score[currentPlayer.id] == 1
		afterComplete.atmosphereLevel == 2
	}

	def 'refuses current card without dropping below zero points'() {
		given:
		def started = startedTwoPlayerSession()
		def currentPlayer = started.players.find { it.id == started.currentTurnPlayerId }
		def otherPlayer = started.players.find { it.id != currentPlayer.id }
		def cardInstance = playableCard(currentPlayer)
		assert post("/api/game-sessions/${started.id}/cards/play",
				playCardBody(currentPlayer, cardInstance, otherPlayer)).status == 200

		when:
		def response = post("/api/game-sessions/${started.id}/current-card/refuse", [
				playerId: currentPlayer.id
		])

		then:
		response.status == 200

		and:
		def afterRefuse = response.body
		afterRefuse.currentCard == null
		afterRefuse.currentTurnPlayerId == otherPlayer.id
		afterRefuse.score[currentPlayer.id] == 0
	}

	def 'rolls a six-sided die for an active session'() {
		given:
		def started = startedTwoPlayerSession()

		when:
		def response = post("/api/game-sessions/${started.id}/dice-rolls", [
				playerId: started.currentTurnPlayerId
		])

		then:
		response.status == 200

		and:
		def roll = response.body
		roll.sessionId == started.id
		roll.playerId == started.currentTurnPlayerId
		roll.value >= 1
		roll.value <= 6
	}

	private createSession(nickname) {
		def response = post('/api/game-sessions', [
				hostNickname : nickname,
				confirmedAdult: true
		])
		assert response.status == 201
		response.body
	}

	private addPlayer(sessionId, nickname) {
		def response = post("/api/game-sessions/${sessionId}/players", [
				nickname      : nickname,
				confirmedAdult: true
		])
		assert response.status == 200
		response.body
	}

	private startedTwoPlayerSession() {
		def session = createSession('Host')
		addPlayer(session.id, 'Guest')
		def response = post("/api/game-sessions/${session.id}/start", null)
		assert response.status == 200
		response.body
	}

	private post(path, body) {
		def connection = "http://localhost:${port}${path}".toURL().openConnection()
		connection.requestMethod = 'POST'
		connection.doOutput = true
		connection.setRequestProperty('Content-Type', 'application/json')
		connection.outputStream.withWriter('UTF-8') { writer ->
			writer << (body == null ? '' : JsonOutput.toJson(body))
		}
		def responseBody = readResponseBody(connection)
		[
				status: connection.responseCode,
				body  : responseBody.isBlank() ? null : new JsonSlurper().parseText(responseBody)
		]
	}

	private static readResponseBody(connection) {
		def stream = connection.responseCode >= 400 ? connection.errorStream : connection.inputStream
		stream == null ? '' : stream.getText('UTF-8')
	}

	private static playableCard(player) {
		player.hand.find { it.card.target != 'chosen_player' } ?: player.hand[0]
	}

	private static playCardBody(currentPlayer, cardInstance, otherPlayer) {
		def body = [
				playerId      : currentPlayer.id,
				cardInstanceId: cardInstance.instanceId
		]
		if (cardInstance.card.target == 'chosen_player') {
			body.targetPlayerId = otherPlayer.id
		}
		body
	}
}
